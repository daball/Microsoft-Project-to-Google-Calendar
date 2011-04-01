/*
  Copyright (C) 2002-2010 Jeroen Frijters

  This software is provided 'as-is', without any express or implied
  warranty.  In no event will the authors be held liable for any damages
  arising from the use of this software.

  Permission is granted to anyone to use this software for any purpose,
  including commercial applications, and to alter it and redistribute it
  freely, subject to the following restrictions:

  1. The origin of this software must not be misrepresented; you must not
     claim that you wrote the original software. If you use this software
     in a product, an acknowledgment in the product documentation would be
     appreciated but is not required.
  2. Altered source versions must be plainly marked as such, and must not be
     misrepresented as being the original software.
  3. This notice may not be removed or altered from any source distribution.

  Jeroen Frijters
  jeroen@frijters.net
  
*/

using System;
using System.Collections.Generic;
using System.Xml.Serialization;
using IKVM.Reflection;
using IKVM.Reflection.Emit;
using Type = IKVM.Reflection.Type;
using System.Diagnostics;
using IKVM.Attributes;
using IKVM.Internal;

namespace IKVM.Internal.MapXml
{
	class CodeGenContext
	{
		private ClassLoaderWrapper classLoader;
		private readonly Dictionary<string, object> h = new Dictionary<string, object>();

		internal CodeGenContext(ClassLoaderWrapper classLoader)
		{
			this.classLoader = classLoader;
		}

		internal object this[string key]
		{
			get
			{
				object val;
				h.TryGetValue(key, out val);
				return val;
			}
			set { h[key] = value; }
		}

		internal ClassLoaderWrapper ClassLoader { get { return classLoader; } }
	}

	public abstract class Instruction
	{
		private int lineNumber = Root.LineNumber;

		internal int LineNumber
		{
			get
			{
				return lineNumber;
			}
		}

		internal abstract void Generate(CodeGenContext context, CodeEmitter ilgen);
	}

	[XmlType("ldstr")]
	public sealed class Ldstr : Instruction
	{
		[XmlAttribute("value")]
		public string Value;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			ilgen.Emit(OpCodes.Ldstr, Value);
		}
	}

	[XmlType("ldnull")]
	public sealed class Ldnull : Simple
	{
		public Ldnull() : base(OpCodes.Ldnull)
		{
		}
	}

	[XmlType("call")]
	public class Call : Instruction
	{
		public Call() : this(OpCodes.Call)
		{
		}

		internal Call(OpCode opcode)
		{
			this.opcode = opcode;
		}

		[XmlAttribute("class")]
		public string Class;
		[XmlAttribute("type")]
		public string type;
		[XmlAttribute("name")]
		public string Name;
		[XmlAttribute("sig")]
		public string Sig;

		private OpCode opcode;

		internal sealed override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			Debug.Assert(Name != null);
			if(Name == ".ctor")
			{
				Debug.Assert(Class == null && type != null);
				Type[] argTypes = context.ClassLoader.ArgTypeListFromSig(Sig);
				ConstructorInfo ci = StaticCompiler.GetTypeForMapXml(context.ClassLoader, type).GetConstructor(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance, null, CallingConventions.Standard, argTypes, null);
				if(ci == null)
				{
					throw new InvalidOperationException("Missing .ctor: " + type + "..ctor" + Sig);
				}
				ilgen.Emit(opcode, ci);
			}
			else
			{
				Debug.Assert(Class == null ^ type == null);
				if(Class != null)
				{
					Debug.Assert(Sig != null);
					MethodWrapper method = context.ClassLoader.LoadClassByDottedName(Class).GetMethodWrapper(Name, Sig, false);
					if(method == null)
					{
						throw new InvalidOperationException("method not found: " + Class + "." + Name + Sig);
					}
					method.Link();
					// TODO this code is part of what Compiler.CastInterfaceArgs (in compiler.cs) does,
					// it would be nice if we could avoid this duplication...
					TypeWrapper[] argTypeWrappers = method.GetParameters();
					for(int i = 0; i < argTypeWrappers.Length; i++)
					{
						if(argTypeWrappers[i].IsGhost)
						{
							CodeEmitterLocal[] temps = new CodeEmitterLocal[argTypeWrappers.Length + (method.IsStatic ? 0 : 1)];
							for(int j = temps.Length - 1; j >= 0; j--)
							{
								TypeWrapper tw;
								if(method.IsStatic)
								{
									tw = argTypeWrappers[j];
								}
								else
								{
									if(j == 0)
									{
										tw = method.DeclaringType;
									}
									else
									{
										tw = argTypeWrappers[j - 1];
									}
								}
								if(tw.IsGhost)
								{
									tw.EmitConvStackTypeToSignatureType(ilgen, null);
								}
								temps[j] = ilgen.DeclareLocal(tw.TypeAsSignatureType);
								ilgen.Emit(OpCodes.Stloc, temps[j]);
							}
							for(int j = 0; j < temps.Length; j++)
							{
								ilgen.Emit(OpCodes.Ldloc, temps[j]);
							}
							break;
						}
					}
					if(opcode.Value == OpCodes.Call.Value)
					{
						method.EmitCall(ilgen);
					}
					else if(opcode.Value == OpCodes.Callvirt.Value)
					{
						method.EmitCallvirt(ilgen);
					}
					else if(opcode.Value == OpCodes.Newobj.Value)
					{
						method.EmitNewobj(ilgen);
					}
					else
					{
						// ldftn or ldvirtftn
						ilgen.Emit(opcode, (MethodInfo)method.GetMethod());
					}
				}
				else
				{
					Type[] argTypes;
					if(Sig.StartsWith("("))
					{
						argTypes = context.ClassLoader.ArgTypeListFromSig(Sig);
					}
					else if(Sig == "")
					{
						argTypes = Type.EmptyTypes;
					}
					else
					{
						string[] types = Sig.Split(';');
						argTypes = new Type[types.Length];
						for(int i = 0; i < types.Length; i++)
						{
							argTypes[i] = StaticCompiler.GetTypeForMapXml(context.ClassLoader, types[i]);
						}
					}
					MethodInfo mi = StaticCompiler.GetTypeForMapXml(context.ClassLoader, type).GetMethod(Name, BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.Static, null, argTypes, null);
					if(mi == null)
					{
						throw new InvalidOperationException("Missing method: " + type + "." + Name + Sig);
					}
					ilgen.Emit(opcode, mi);
				}
			}
		}
	}

	[XmlType("callvirt")]
	public sealed class Callvirt : Call
	{
		public Callvirt() : base(OpCodes.Callvirt)
		{
		}
	}

	[XmlType("newobj")]
	public sealed class NewObj : Call
	{
		public NewObj() : base(OpCodes.Newobj)
		{
		}
	}

	[XmlType("ldftn")]
	public sealed class Ldftn : Call
	{
		public Ldftn() : base(OpCodes.Ldftn)
		{
		}
	}

	[XmlType("ldvirtftn")]
	public sealed class Ldvirtftn : Call
	{
		public Ldvirtftn() : base(OpCodes.Ldvirtftn)
		{
		}
	}

	public abstract class Simple : Instruction
	{
		private OpCode opcode;

		public Simple(OpCode opcode)
		{
			this.opcode = opcode;
		}

		internal sealed override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			ilgen.Emit(opcode);
		}
	}

	[XmlType("dup")]
	public sealed class Dup : Simple
	{
		public Dup() : base(OpCodes.Dup)
		{
		}
	}

	[XmlType("pop")]
	public sealed class Pop : Instruction
	{
		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			ilgen.Emit(OpCodes.Pop);
		}
	}

	public abstract class TypeOrTypeWrapperInstruction : Instruction
	{
		[XmlAttribute("class")]
		public string Class;
		[XmlAttribute("type")]
		public string type;

		internal TypeWrapper typeWrapper;
		internal Type typeType;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			if(typeWrapper == null && typeType == null)
			{
				Debug.Assert(Class == null ^ type == null);
				if(Class != null)
				{
					typeWrapper = context.ClassLoader.LoadClassByDottedName(Class);
				}
				else
				{
					typeType = StaticCompiler.GetTypeForMapXml(context.ClassLoader, type);
				}
			}
		}
	}

	[XmlType("isinst")]
	public sealed class IsInst : TypeOrTypeWrapperInstruction
	{
		public IsInst()
		{
		}

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			base.Generate(context, ilgen);
			if(typeType != null)
			{
				ilgen.Emit(OpCodes.Isinst, typeType);
			}
			else
			{
				if(typeWrapper.IsGhost || typeWrapper.IsGhostArray)
				{
					ilgen.Emit(OpCodes.Dup);
					// NOTE we pass a null context, but that shouldn't be a problem, because
					// typeWrapper should never be an UnloadableTypeWrapper
					typeWrapper.EmitInstanceOf(null, ilgen);
					CodeEmitterLabel endLabel = ilgen.DefineLabel();
					ilgen.Emit(OpCodes.Brtrue_S, endLabel);
					ilgen.Emit(OpCodes.Pop);
					ilgen.Emit(OpCodes.Ldnull);
					ilgen.MarkLabel(endLabel);
				}
				else
				{
					ilgen.Emit(OpCodes.Isinst, typeWrapper.TypeAsTBD);
				}
			}
		}
	}

	[XmlType("castclass")]
	public sealed class Castclass : TypeOrTypeWrapperInstruction
	{
		public Castclass()
		{
		}

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			base.Generate(context, ilgen);
			if(typeType != null)
			{
				ilgen.Emit(OpCodes.Castclass, typeType);
			}
			else
			{
				// NOTE we pass a null context, but that shouldn't be a problem, because
				// typeWrapper should never be an UnloadableTypeWrapper
				typeWrapper.EmitCheckcast(null, ilgen);
			}
		}
	}

	[XmlType("castclass_impl")]
	public sealed class Castclass_impl : Instruction
	{
		[XmlAttribute("class")]
		public string Class;

		public Castclass_impl()
		{
		}

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			ilgen.Emit(OpCodes.Castclass, context.ClassLoader.LoadClassByDottedName(Class).TypeAsBaseType);
		}
	}

	public abstract class TypeInstruction : Instruction
	{
		[XmlAttribute("type")]
		public string type;

		private OpCode opcode;
		private Type typeType;

		internal TypeInstruction(OpCode opcode)
		{
			this.opcode = opcode;
		}

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			if(typeType == null)
			{
				Debug.Assert(type != null);
				typeType = StaticCompiler.GetTypeForMapXml(context.ClassLoader, type);
			}
			ilgen.Emit(opcode, typeType);
		}
	}

	[XmlType("ldobj")]
	public sealed class Ldobj : TypeInstruction
	{
		public Ldobj() : base(OpCodes.Ldobj)
		{
		}
	}

	[XmlType("unbox")]
	public sealed class Unbox : TypeInstruction
	{
		public Unbox() : base(OpCodes.Unbox)
		{
		}
	}

	[XmlType("box")]
	public sealed class Box : TypeInstruction
	{
		public Box() : base(OpCodes.Box)
		{
		}
	}

	public abstract class Branch : Instruction
	{
		private OpCode opcode;

		public Branch(OpCode opcode)
		{
			this.opcode = opcode;
		}

		internal sealed override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			CodeEmitterLabel l;
			if(context[Name] == null)
			{
				l = ilgen.DefineLabel();
				context[Name] = l;
			}
			else
			{
				l = (CodeEmitterLabel)context[Name];
			}
			ilgen.Emit(opcode, l);
		}

		[XmlAttribute("name")]
		public string Name;
	}

	[XmlType("brfalse")]
	public sealed class BrFalse : Branch
	{
		public BrFalse() : base(OpCodes.Brfalse)
		{
		}
	}

	[XmlType("brtrue")]
	public sealed class BrTrue : Branch
	{
		public BrTrue() : base(OpCodes.Brtrue)
		{
		}
	}

	[XmlType("br")]
	public sealed class Br : Branch
	{
		public Br() : base(OpCodes.Br)
		{
		}
	}

	[XmlType("beq")]
	public sealed class Beq : Branch
	{
		public Beq()
			: base(OpCodes.Beq)
		{
		}
	}

	[XmlType("bne_un")]
	public sealed class Bne_Un : Branch
	{
		public Bne_Un()
			: base(OpCodes.Bne_Un)
		{
		}
	}

	[XmlType("bge_un")]
	public sealed class Bge_Un : Branch
	{
		public Bge_Un() : base(OpCodes.Bge_Un)
		{
		}
	}

	[XmlType("ble_un")]
	public sealed class Ble_Un : Branch
	{
		public Ble_Un() : base(OpCodes.Ble_Un)
		{
		}
	}

	[XmlType("blt")]
	public sealed class Blt : Branch
	{
		public Blt() : base(OpCodes.Blt)
		{
		}
	}

	[XmlType("blt_un")]
	public sealed class Blt_Un : Branch
	{
		public Blt_Un() : base(OpCodes.Blt_Un)
		{
		}
	}

	[XmlType("label")]
	public sealed class BrLabel : Instruction
	{
		[XmlAttribute("name")]
		public string Name;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			CodeEmitterLabel l;
			if(context[Name] == null)
			{
				l = ilgen.DefineLabel();
				context[Name] = l;
			}
			else
			{
				l = (CodeEmitterLabel)context[Name];
			}
			ilgen.MarkLabel(l);
		}
	}

	[XmlType("stloc")]
	public sealed class StLoc : Instruction
	{
		[XmlAttribute("name")]
		public string Name;
		[XmlAttribute("class")]
		public string Class;
		[XmlAttribute("type")]
		public string type;

		private TypeWrapper typeWrapper;
		private Type typeType;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			CodeEmitterLocal lb = (CodeEmitterLocal)context[Name];
			if(lb == null)
			{
				if(typeWrapper == null && typeType == null)
				{
					Debug.Assert(Class == null ^ type == null);
					if(type != null)
					{
						typeType = StaticCompiler.GetTypeForMapXml(context.ClassLoader, type);
					}
					else
					{
						typeWrapper = context.ClassLoader.LoadClassByDottedName(Class);
					}
				}
				lb = ilgen.DeclareLocal(typeType != null ? typeType : typeWrapper.TypeAsTBD);
				context[Name] = lb;
			}
			ilgen.Emit(OpCodes.Stloc, lb);
		}
	}

	[XmlType("ldloc")]
	public sealed class LdLoc : Instruction
	{
		[XmlAttribute("name")]
		public string Name;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			ilgen.Emit(OpCodes.Ldloc, (CodeEmitterLocal)context[Name]);
		}
	}

	[XmlType("ldarga")]
	public sealed class LdArga : Instruction
	{
		[XmlAttribute("argNum")]
		public ushort ArgNum;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			ilgen.Emit(OpCodes.Ldarga, (short)ArgNum);
		}
	}

	[XmlType("ldarg_0")]
	public sealed class LdArg_0 : Simple
	{
		public LdArg_0() : base(OpCodes.Ldarg_0)
		{
		}
	}

	[XmlType("ldarg_1")]
	public sealed class LdArg_1 : Simple
	{
		public LdArg_1() : base(OpCodes.Ldarg_1)
		{
		}
	}

	[XmlType("ldarg_2")]
	public sealed class LdArg_2 : Simple
	{
		public LdArg_2() : base(OpCodes.Ldarg_2)
		{
		}
	}

	[XmlType("ldarg_3")]
	public sealed class LdArg_3 : Simple
	{
		public LdArg_3() : base(OpCodes.Ldarg_3)
		{
		}
	}

	[XmlType("ldind_i1")]
	public sealed class Ldind_i1 : Simple
	{
		public Ldind_i1() : base(OpCodes.Ldind_I1)
		{
		}
	}

	[XmlType("ldind_i2")]
	public sealed class Ldind_i2 : Simple
	{
		public Ldind_i2() : base(OpCodes.Ldind_I2)
		{
		}
	}

	[XmlType("ldind_i4")]
	public sealed class Ldind_i4 : Simple
	{
		public Ldind_i4() : base(OpCodes.Ldind_I4)
		{
		}
	}

	[XmlType("ldind_i8")]
	public sealed class Ldind_i8 : Simple
	{
		public Ldind_i8() : base(OpCodes.Ldind_I8)
		{
		}
	}

	[XmlType("ldind_r4")]
	public sealed class Ldind_r4 : Simple
	{
		public Ldind_r4() : base(OpCodes.Ldind_R4)
		{
		}
	}

	[XmlType("ldind_r8")]
	public sealed class Ldind_r8 : Simple
	{
		public Ldind_r8() : base(OpCodes.Ldind_R8)
		{
		}
	}

	[XmlType("ldind_ref")]
	public sealed class Ldind_ref : Simple
	{
		public Ldind_ref() : base(OpCodes.Ldind_Ref)
		{
		}
	}

	[XmlType("stind_i1")]
	public sealed class Stind_i1 : Simple
	{
		public Stind_i1() : base(OpCodes.Stind_I1)
		{
		}
	}

	[XmlType("stind_i2")]
	public sealed class Stind_i2 : Simple
	{
		public Stind_i2() : base(OpCodes.Stind_I2)
		{
		}
	}

	[XmlType("stind_i4")]
	public sealed class Stind_i4 : Simple
	{
		public Stind_i4() : base(OpCodes.Stind_I4)
		{
		}
	}

	[XmlType("stind_ref")]
	public sealed class Stind_ref : Simple
	{
		public Stind_ref() : base(OpCodes.Stind_Ref)
		{
		}
	}

	[XmlType("ret")]
	public sealed class Ret : Simple
	{
		public Ret() : base(OpCodes.Ret)
		{
		}
	}

	[XmlType("throw")]
	public sealed class Throw : Simple
	{
		public Throw() : base(OpCodes.Throw)
		{
		}
	}

	[XmlType("ldflda")]
	public sealed class Ldflda : Instruction
	{
		[XmlAttribute("class")]
		public string Class;
		[XmlAttribute("name")]
		public string Name;
		[XmlAttribute("sig")]
		public string Sig;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			FieldWrapper fw = StaticCompiler.GetClassForMapXml(context.ClassLoader, Class).GetFieldWrapper(Name, Sig);
			fw.Link();
			ilgen.Emit(OpCodes.Ldflda, fw.GetField());
		}
	}

	[XmlType("ldfld")]
	public sealed class Ldfld : Instruction
	{
		[XmlAttribute("class")]
		public string Class;
		[XmlAttribute("name")]
		public string Name;
		[XmlAttribute("sig")]
		public string Sig;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			FieldWrapper fw = StaticCompiler.GetClassForMapXml(context.ClassLoader, Class).GetFieldWrapper(Name, Sig);
			fw.Link();
			// we don't use fw.EmitGet because we don't want automatic unboxing and whatever
			ilgen.Emit(OpCodes.Ldfld, fw.GetField());
		}
	}

	[XmlType("ldsfld")]
	public sealed class Ldsfld : Instruction
	{
		[XmlAttribute("class")]
		public string Class;
		[XmlAttribute("type")]
		public string Type;
		[XmlAttribute("name")]
		public string Name;
		[XmlAttribute("sig")]
		public string Sig;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			if(Type != null)
			{
				ilgen.Emit(OpCodes.Ldsfld, StaticCompiler.GetTypeForMapXml(context.ClassLoader, Type).GetField(Name, BindingFlags.Static | BindingFlags.Public | BindingFlags.NonPublic));
			}
			else
			{
				FieldWrapper fw = StaticCompiler.GetClassForMapXml(context.ClassLoader, Class).GetFieldWrapper(Name, Sig);
				fw.Link();
				// we don't use fw.EmitGet because we don't want automatic unboxing and whatever
				ilgen.Emit(OpCodes.Ldsfld, fw.GetField());
			}
		}
	}

	[XmlType("stfld")]
	public sealed class Stfld : Instruction
	{
		[XmlAttribute("class")]
		public string Class;
		[XmlAttribute("name")]
		public string Name;
		[XmlAttribute("sig")]
		public string Sig;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			FieldWrapper fw = StaticCompiler.GetClassForMapXml(context.ClassLoader, Class).GetFieldWrapper(Name, Sig);
			fw.Link();
			// we don't use fw.EmitSet because we don't want automatic unboxing and whatever
			ilgen.Emit(OpCodes.Stfld, fw.GetField());
		}
	}

	[XmlType("stsfld")]
	public sealed class Stsfld : Instruction
	{
		[XmlAttribute("class")]
		public string Class;
		[XmlAttribute("name")]
		public string Name;
		[XmlAttribute("sig")]
		public string Sig;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			FieldWrapper fw = StaticCompiler.GetClassForMapXml(context.ClassLoader, Class).GetFieldWrapper(Name, Sig);
			fw.Link();
			// we don't use fw.EmitSet because we don't want automatic unboxing and whatever
			ilgen.Emit(OpCodes.Stsfld, fw.GetField());
		}
	}

	[XmlType("ldc_i4")]
	public sealed class Ldc_I4 : Instruction
	{
		[XmlAttribute("value")]
		public int val;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			ilgen.Emit(OpCodes.Ldc_I4, val);
		}
	}

	[XmlType("ldc_i4_0")]
	public sealed class Ldc_I4_0 : Simple
	{
		public Ldc_I4_0() : base(OpCodes.Ldc_I4_0)
		{
		}
	}

	[XmlType("ldc_i4_1")]
	public sealed class Ldc_I4_1 : Simple
	{
		public Ldc_I4_1() : base(OpCodes.Ldc_I4_1)
		{
		}
	}

	[XmlType("ldc_i4_m1")]
	public sealed class Ldc_I4_M1 : Simple
	{
		public Ldc_I4_M1() : base(OpCodes.Ldc_I4_M1)
		{
		}
	}

	[XmlType("conv_i")]
	public sealed class Conv_I : Simple
	{
		public Conv_I() : base(OpCodes.Conv_I)
		{
		}
	}

	[XmlType("conv_i1")]
	public sealed class Conv_I1 : Simple
	{
		public Conv_I1() : base(OpCodes.Conv_I1)
		{
		}
	}

	[XmlType("conv_u1")]
	public sealed class Conv_U1 : Simple
	{
		public Conv_U1() : base(OpCodes.Conv_U1)
		{
		}
	}

	[XmlType("conv_i2")]
	public sealed class Conv_I2 : Simple
	{
		public Conv_I2() : base(OpCodes.Conv_I2)
		{
		}
	}

	[XmlType("conv_u2")]
	public sealed class Conv_U2 : Simple
	{
		public Conv_U2() : base(OpCodes.Conv_U2)
		{
		}
	}

	[XmlType("conv_i4")]
	public sealed class Conv_I4 : Simple
	{
		public Conv_I4() : base(OpCodes.Conv_I4)
		{
		}
	}

	[XmlType("conv_u4")]
	public sealed class Conv_U4 : Simple
	{
		public Conv_U4() : base(OpCodes.Conv_U4)
		{
		}
	}

	[XmlType("conv_i8")]
	public sealed class Conv_I8 : Simple
	{
		public Conv_I8() : base(OpCodes.Conv_I8)
		{
		}
	}

	[XmlType("conv_u8")]
	public sealed class Conv_U8 : Simple
	{
		public Conv_U8() : base(OpCodes.Conv_U8)
		{
		}
	}

	[XmlType("ldlen")]
	public sealed class Ldlen : Simple
	{
		public Ldlen() : base(OpCodes.Ldlen)
		{
		}
	}

	[XmlType("add")]
	public sealed class Add : Simple
	{
		public Add() : base(OpCodes.Add)
		{
		}
	}

	[XmlType("sub")]
	public sealed class Sub : Simple
	{
		public Sub()
			: base(OpCodes.Sub)
		{
		}
	}

	[XmlType("mul")]
	public sealed class Mul : Simple
	{
		public Mul() : base(OpCodes.Mul)
		{
		}
	}

	[XmlType("and")]
	public sealed class And : Simple
	{
		public And()
			: base(OpCodes.And)
		{
		}
	}

	[XmlType("unaligned")]
	public sealed class Unaligned : Instruction
	{
		[XmlAttribute("alignment")]
		public int Alignment;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			ilgen.Emit(OpCodes.Unaligned, (byte)Alignment);
		}
	}

	[XmlType("cpblk")]
	public sealed class Cpblk : Simple
	{
		public Cpblk() : base(OpCodes.Cpblk)
		{
		}
	}

	[XmlType("ceq")]
	public sealed class Ceq : Simple
	{
		public Ceq() : base(OpCodes.Ceq)
		{
		}
	}

	[XmlType("leave")]
	public sealed class Leave : Branch
	{
		public Leave() : base(OpCodes.Leave)
		{
		}
	}

	[XmlType("endfinally")]
	public sealed class Endfinally : Simple
	{
		public Endfinally() : base(OpCodes.Endfinally)
		{
		}
	}

	[XmlType("exceptionBlock")]
	public sealed class ExceptionBlock : Instruction
	{
		public InstructionList @try;
		public CatchBlock @catch;
		public InstructionList @finally;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			ilgen.BeginExceptionBlock();
			@try.Generate(context, ilgen);
			if(@catch != null)
			{
				Type type;
				if(@catch.type != null)
				{
					type = StaticCompiler.GetTypeForMapXml(context.ClassLoader, @catch.type);
				}
				else
				{
					type = context.ClassLoader.LoadClassByDottedName(@catch.Class).TypeAsExceptionType;
				}
				ilgen.BeginCatchBlock(type);
				@catch.Generate(context, ilgen);
			}
			if(@finally != null)
			{
				ilgen.BeginFinallyBlock();
				@finally.Generate(context, ilgen);
			}
			ilgen.EndExceptionBlock();
		}
	}

	public class CatchBlock : InstructionList
	{
		[XmlAttribute("type")]
		public string type;
		[XmlAttribute("class")]
		public string Class;
	}

	[XmlType("conditional")]
	public class ConditionalInstruction : Instruction
	{
		[XmlAttribute("framework")]
		public string framework;
		public InstructionList code;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			if (Environment.Version.ToString().StartsWith(framework))
			{
				code.Generate(context, ilgen);
			}
		}
	}

	[XmlType("volatile")]
	public sealed class Volatile : Simple
	{
		public Volatile() : base(OpCodes.Volatile)
		{
		}
	}

	[XmlType("ldelema")]
	public sealed class Ldelema : Instruction
	{
		[XmlAttribute("sig")]
		public string Sig;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			ilgen.Emit(OpCodes.Ldelema, context.ClassLoader.FieldTypeWrapperFromSig(Sig).TypeAsArrayType);
		}
	}

	[XmlType("newarr")]
	public sealed class Newarr : Instruction
	{
		[XmlAttribute("sig")]
		public string Sig;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			ilgen.Emit(OpCodes.Newarr, context.ClassLoader.FieldTypeWrapperFromSig(Sig).TypeAsArrayType);
		}
	}

	[XmlType("ldtoken")]
	public sealed class Ldtoken : Instruction
	{
		[XmlAttribute("type")]
		public string type;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			ilgen.Emit(OpCodes.Ldtoken, StaticCompiler.GetTypeForMapXml(context.ClassLoader, type));
		}
	}

	[XmlType("runclassinit")]
	public sealed class RunClassInit : Instruction
	{
		[XmlAttribute("class")]
		public string Class;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			context.ClassLoader.LoadClassByDottedName(Class).EmitRunClassConstructor(ilgen);
		}
	}

	[XmlType("exceptionMapping")]
	public sealed class EmitExceptionMapping : Instruction
	{
		internal ExceptionMapping[] mapping;

		internal override void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			CompilerClassLoader.ExceptionMapEmitter emitter = new CompilerClassLoader.ExceptionMapEmitter(mapping);
			emitter.Emit(context, ilgen);
		}
	}

	public class InstructionList
	{
		[XmlElement(typeof(Ldstr))]
		[XmlElement(typeof(Call))]
		[XmlElement(typeof(Callvirt))]
		[XmlElement(typeof(Ldftn))]
		[XmlElement(typeof(Ldvirtftn))]
		[XmlElement(typeof(Dup))]
		[XmlElement(typeof(Pop))]
		[XmlElement(typeof(IsInst))]
		[XmlElement(typeof(Castclass))]
		[XmlElement(typeof(Castclass_impl))]
		[XmlElement(typeof(Ldobj))]
		[XmlElement(typeof(Unbox))]
		[XmlElement(typeof(Box))]
		[XmlElement(typeof(BrFalse))]
		[XmlElement(typeof(BrTrue))]
		[XmlElement(typeof(Br))]
		[XmlElement(typeof(Beq))]
		[XmlElement(typeof(Bne_Un))]
		[XmlElement(typeof(Bge_Un))]
		[XmlElement(typeof(Ble_Un))]
		[XmlElement(typeof(Blt))]
		[XmlElement(typeof(Blt_Un))]
		[XmlElement(typeof(BrLabel))]
		[XmlElement(typeof(NewObj))]
		[XmlElement(typeof(StLoc))]
		[XmlElement(typeof(LdLoc))]
		[XmlElement(typeof(LdArga))]
		[XmlElement(typeof(LdArg_0))]
		[XmlElement(typeof(LdArg_1))]
		[XmlElement(typeof(LdArg_2))]
		[XmlElement(typeof(LdArg_3))]
		[XmlElement(typeof(Ldind_i1))]
		[XmlElement(typeof(Ldind_i2))]
		[XmlElement(typeof(Ldind_i4))]
		[XmlElement(typeof(Ldind_i8))]
		[XmlElement(typeof(Ldind_r4))]
		[XmlElement(typeof(Ldind_r8))]
		[XmlElement(typeof(Ldind_ref))]
		[XmlElement(typeof(Stind_i1))]
		[XmlElement(typeof(Stind_i2))]
		[XmlElement(typeof(Stind_i4))]
		[XmlElement(typeof(Stind_ref))]
		[XmlElement(typeof(Ret))]
		[XmlElement(typeof(Throw))]
		[XmlElement(typeof(Ldnull))]
		[XmlElement(typeof(Ldflda))]
		[XmlElement(typeof(Ldfld))]
		[XmlElement(typeof(Ldsfld))]
		[XmlElement(typeof(Stfld))]
		[XmlElement(typeof(Stsfld))]
		[XmlElement(typeof(Ldc_I4))]
		[XmlElement(typeof(Ldc_I4_0))]
		[XmlElement(typeof(Ldc_I4_1))]
		[XmlElement(typeof(Ldc_I4_M1))]
		[XmlElement(typeof(Conv_I))]
		[XmlElement(typeof(Conv_I1))]
		[XmlElement(typeof(Conv_U1))]
		[XmlElement(typeof(Conv_I2))]
		[XmlElement(typeof(Conv_U2))]
		[XmlElement(typeof(Conv_I4))]
		[XmlElement(typeof(Conv_U4))]
		[XmlElement(typeof(Conv_I8))]
		[XmlElement(typeof(Conv_U8))]
		[XmlElement(typeof(Ldlen))]
		[XmlElement(typeof(ExceptionBlock))]
		[XmlElement(typeof(Add))]
		[XmlElement(typeof(Sub))]
		[XmlElement(typeof(Mul))]
		[XmlElement(typeof(And))]
		[XmlElement(typeof(Unaligned))]
		[XmlElement(typeof(Cpblk))]
		[XmlElement(typeof(Ceq))]
		[XmlElement(typeof(ConditionalInstruction))]
		[XmlElement(typeof(Volatile))]
		[XmlElement(typeof(Ldelema))]
		[XmlElement(typeof(Newarr))]
		[XmlElement(typeof(Ldtoken))]
		[XmlElement(typeof(Leave))]
		[XmlElement(typeof(Endfinally))]
		[XmlElement(typeof(RunClassInit))]
		[XmlElement(typeof(EmitExceptionMapping))]
		public Instruction[] invoke;

		internal void Generate(CodeGenContext context, CodeEmitter ilgen)
		{
			if(invoke != null)
			{
				for(int i = 0; i < invoke.Length; i++)
				{
					if(invoke[i].LineNumber != -1)
					{
						ilgen.SetLineNumber((ushort)invoke[i].LineNumber);
					}
					invoke[i].Generate(context, ilgen);
				}
			}
		}

		internal void Emit(ClassLoaderWrapper loader, CodeEmitter ilgen)
		{
			Generate(new CodeGenContext(loader), ilgen);
		}
	}

	public class Throws
	{
		[XmlAttribute("class")]
		public string Class;
	}

	public class Constructor
	{
		[XmlAttribute("sig")]
		public string Sig;
		[XmlAttribute("modifiers")]
		public MapModifiers Modifiers;
		[XmlElement("parameter")]
		public Param[] Params;
		public InstructionList body;
		public InstructionList alternateBody;
		public Redirect redirect;
		[XmlElement("throws", typeof(Throws))]
		public Throws[] throws;
		[XmlElement("attribute")]
		public Attribute[] Attributes;
	}

	public class Redirect
	{
		private int linenum = Root.LineNumber;

		internal int LineNumber
		{
			get
			{
				return linenum;
			}
		}

		[XmlAttribute("class")]
		public string Class;
		[XmlAttribute("name")]
		public string Name;
		[XmlAttribute("sig")]
		public string Sig;
		[XmlAttribute("type")]
		public string Type;

		internal void Emit(ClassLoaderWrapper loader, CodeEmitter ilgen)
		{
			if(Type != "static" || Class == null || Name == null || Sig == null)
			{
				throw new NotImplementedException();
			}
			Type[] redirParamTypes = loader.ArgTypeListFromSig(Sig);
			for(int i = 0; i < redirParamTypes.Length; i++)
			{
				ilgen.Emit(OpCodes.Ldarg, (short)i);
			}
			// HACK if the class name contains a comma, we assume it is a .NET type
			if(Class.IndexOf(',') >= 0)
			{
				Type type = StaticCompiler.Universe.GetType(Class, true);
				MethodInfo mi = type.GetMethod(Name, redirParamTypes);
				if(mi == null)
				{
					throw new InvalidOperationException();
				}
				ilgen.Emit(OpCodes.Call, mi);
			}
			else
			{
				TypeWrapper tw = loader.LoadClassByDottedName(Class);
				MethodWrapper mw = tw.GetMethodWrapper(Name, Sig, false);
				if(mw == null)
				{
					throw new InvalidOperationException();
				}
				mw.Link();
				mw.EmitCall(ilgen);
			}
			// TODO we may need a cast here (or a stack to return type conversion)
			ilgen.Emit(OpCodes.Ret);
		}
	}

	public class Override
	{
		[XmlAttribute("class")]
		public string Class;
		[XmlAttribute("name")]
		public string Name;
	}

	public class ReplaceMethodCall
	{
		[XmlAttribute("class")]
		public string Class;
		[XmlAttribute("name")]
		public string Name;
		[XmlAttribute("sig")]
		public string Sig;
		public InstructionList code;
	}

	public class Method
	{
		[XmlAttribute("name")]
		public string Name;
		[XmlAttribute("sig")]
		public string Sig;
		[XmlAttribute("modifiers")]
		public MapModifiers Modifiers;
		[XmlAttribute("attributes")]
		public MethodAttributes MethodAttributes;
		[XmlAttribute("nonullcheck")]
		public bool NoNullCheck;
		[XmlElement("parameter")]
		public Param[] Params;
		public InstructionList body;
		public InstructionList alternateBody;
		public InstructionList nonvirtualAlternateBody;
		public Redirect redirect;
		public Override @override;
		[XmlElement("throws", typeof(Throws))]
		public Throws[] throws;
		[XmlElement("attribute")]
		public Attribute[] Attributes;
		[XmlElement("replace-method-call")]
		public ReplaceMethodCall[] ReplaceMethodCalls;

		internal void Emit(ClassLoaderWrapper loader, CodeEmitter ilgen)
		{
			if(redirect != null)
			{
				redirect.Emit(loader, ilgen);
			}
			else
			{
				body.Emit(loader, ilgen);
			}
		}
	}

	public class Field
	{
		[XmlAttribute("name")]
		public string Name;
		[XmlAttribute("sig")]
		public string Sig;
		[XmlAttribute("modifiers")]
		public MapModifiers Modifiers;
		[XmlAttribute("constant")]
		public string Constant;
		public Redirect redirect;
		[XmlElement("attribute")]
		public Attribute[] Attributes;
	}

	public class Property
	{
		[XmlAttribute("name")]
		public string Name;
		[XmlAttribute("sig")]
		public string Sig;
		public Method getter;
		public Method setter;
		[XmlElement("attribute")]
		public Attribute[] Attributes;
	}

	public class Interface
	{
		[XmlAttribute("class")]
		public string Name;
		[XmlElement("method")]
		public Method[] Methods;
	}

	[Flags]
	public enum MapModifiers
	{
		[XmlEnum("public")]
		Public = Modifiers.Public,
		[XmlEnum("protected")]
		Protected = Modifiers.Protected,
		[XmlEnum("private")]
		Private = Modifiers.Private,
		[XmlEnum("final")]
		Final = Modifiers.Final,
		[XmlEnum("interface")]
		Interface = Modifiers.Interface,
		[XmlEnum("static")]
		Static = Modifiers.Static,
		[XmlEnum("abstract")]
		Abstract = Modifiers.Abstract
	}

	public enum Scope
	{
		[XmlEnum("public")]
		Public = 0,
		[XmlEnum("private")]
		Private = 1
	}

	public class Element
	{
		[XmlText]
		public string Value;
	}

	public class Param
	{
		[XmlText]
		public string Value;
		[XmlAttribute("name")]
		public string Name;
		[XmlAttribute("sig")]
		public string Sig;		// optional (for object type args)
		[XmlElement("element")]
		public Element[] Elements;
		[XmlElement("attribute")]
		public Attribute[] Attributes;
	}

	public class Attribute
	{
		[XmlAttribute("type")]
		public string Type;
		[XmlAttribute("class")]
		public string Class;
		[XmlAttribute("sig")]
		public string Sig;
		[XmlElement("parameter")]
		public Param[] Params;
		[XmlElement("property")]
		public Param[] Properties;
		[XmlElement("field")]
		public Param[] Fields;
	}

	[XmlType("class")]
	public class Class
	{
		[XmlAttribute("name")]
		public string Name;
		[XmlAttribute("shadows")]
		public string Shadows;
		[XmlAttribute("modifiers")]
		public MapModifiers Modifiers;
		[XmlAttribute("scope")]
		public Scope scope;
		[XmlElement("constructor")]
		public Constructor[] Constructors;
		[XmlElement("method")]
		public Method[] Methods;
		[XmlElement("field")]
		public Field[] Fields;
		[XmlElement("property")]
		public Property[] Properties;
		[XmlElement("implements")]
		public Interface[] Interfaces;
		[XmlElement("clinit")]
		public Method Clinit;
		[XmlElement("attribute")]
		public Attribute[] Attributes;
	}

	public class Assembly
	{
		[XmlElement("class")]
		public Class[] Classes;
		[XmlElement("attribute")]
		public Attribute[] Attributes;
	}

	[XmlType("exception")]
	public class ExceptionMapping
	{
		[XmlAttribute]
		public string src;
		[XmlAttribute]
		public string dst;
		public InstructionList code;
	}

	[XmlRoot("root")]
	public class Root
	{
		internal static System.Xml.XmlTextReader xmlReader;
		internal static string filename;

		internal static int LineNumber
		{
			get
			{
				return xmlReader == null ? -1: xmlReader.LineNumber;
			}
		}

		[XmlElement("assembly")]
		public Assembly assembly;
		public ExceptionMapping[] exceptionMappings;
	}
}
