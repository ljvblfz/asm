package org.objectweb.asm.signature;

/**
 * A signature reader class. This class is intended to be instantiated for each
 * signature that is read. This design enables us to use final fields for the
 * signature and length which is very favorable from an optimization standpoint.
 * Allocating objects is generally very cheap. The class uses very little error
 * checking. Asserts have been added to give some sanity checks when the code is
 * compiled in debug-mode. Invalid signatures might cause an IndexOutOfBounds
 * exception in the read method. This design is intentional in order to get high
 * perfomance and small footprint.
 * 
 * @author Thomas Hallgren
 */
public class SignatureReader {
  
	private final String m_signature;

	private final int m_length;

	private int m_position;

	private char m_char;

	/**
	 * Create a <code>SignatureReader</code> for the given signature.
	 * 
	 * @param signature A <i>ClassSignature </i>, <i>FieldTypeSignature </i>, or
	 *            <i>MethodTypeSignature </i>
	 */
  
	public SignatureReader (String signature) {
		m_signature = signature;
		m_length = signature.length();
		m_position = 0;
	}

	/**
	 * This method is intended to be called on a <code>SignatureReader</code>
	 * that was created using the <i>ClassSignature </i> <code>signature</code>
	 * parameter of the
	 * {@link org.objectweb.asm.ClassVisitor#visit ClassVisitor.visit} method as
	 * the constructor argument.
	 * 
	 * @param v The visitor.
	 */
  
	public void acceptClass (ClassSignatureVisitor v)	{
		read();
		if (m_char == '<') {
			formalTypeParameters(v);
    }
		//assert (m_char == 'L');
		read();
		classTypeSignature(v.visitSuperclass());
		while (m_position < m_length) {
      read();
			classTypeSignature(v.visitInterface());
    }
	}

	/**
	 * This method is intended to be called on a <code>SignatureReader</code>
	 * that was created using the <i>MethodTypeSignature </i>
	 * <code>signature</code> parameter of the
	 * {@link org.objectweb.asm.ClassVisitor#visitMethod ClassVisitor.visitMethod}
	 * method as the constructor argument.
	 * 
	 * @param v The visitor.
	 */
  
	public void acceptMethod (MethodSignatureVisitor v) {
		// MethodTypeSignature:
		//	FormalTypeParameters_opt (TypeSignature*) ReturnType ThrowsSignature*
		//
		// ReturnType:
		//	TypeSignature
		//	VoidDescriptor
		//
		// ThrowsSignature:
		//	ClassTypeSignature
		//	TypeVariableSignature
		//
		read();
		if (m_char == '<') {
			formalTypeParameters(v);
    }
		//assert (m_char == '(');
		read();
		while (m_char != ')') {
			typeSignature(v.visitParameterType());
    }
		read();
		typeSignature(v.visitReturnType());
		while (m_position < m_length) {
      fieldTypeSignature(v.visitExceptionType());
		}
	}

	/**
	 * This method is intended to be called on a <code>SignatureReader</code>
	 * that was created using the <i>FieldTypeSignature </i>
	 * <code>signature</code> parameter of the
	 * {@link org.objectweb.asm.ClassVisitor#visitField ClassVisitor.visitField}
	 * method as the constructor argument.
	 * 
	 * @param v The visitor.
	 */
  
	public void acceptType (TypeSignatureVisitor v) {
		read();
		fieldTypeSignature(v);
	}

	private void formalTypeParameters (MemberSignatureVisitor v) {
		// FormalTypeParameters:
		//	<FormalTypeParameter+>
		//
		read();
		do {
			int startPos = m_position - 1;
			do {
				read();
			} while (m_char != ':');
			v.visitFormalTypeParameter(m_signature.substring(startPos, m_position - 1));
			read();
			formalTypeParameter(v);
		} while (m_char != '>');
		read();
	}

	private void formalTypeParameter (MemberSignatureVisitor v) { // can be inlined
		// FormalTypeParameter:
		//	Identifier ClassBound InterfaceBound*
		// ClassBound:
		//	: FieldTypeSignatureopt
		// InterfaceBound:
		//	: FieldTypeSignature
		//
		// The Identifier and ':' are already consumed.
		//
		switch (m_char) {
			case 'L':
			case '[':
			case 'T':
				fieldTypeSignature(v.visitClassBound());
				if (m_char != ':') {
					return; // We're done. There are no interface bounds
        }
			// Fall through
			case ':':
				do {
					read();
					fieldTypeSignature(v.visitInterfaceBound());
				} while (m_char == ':');
		}
	}

	private void fieldTypeSignature (TypeSignatureVisitor v) {
		// FieldTypeSignature:
		//	ClassTypeSignature
		//	ArrayTypeSignature
		//	TypeVariableSignature
		//
		char c = m_char;
		read();
		switch (c) {
			case 'L':
				classTypeSignature(v);
				break;
			case '[':
				typeSignature(v.visitArrayType());
				break;
			default:
				//assert (c == 'T');
				typeVariableSignature(v);
		}
	}

	private void classTypeSignature (TypeSignatureVisitor v) {
		// ClassTypeSignature:
		//	L PackageSpecifier* SimpleClassTypeSignature ClassTypeSignatureSuffix* ;
		//
		// PackageSpecifier:
		//	Identifier / PackageSpecifier*
		//
		// SimpleClassTypeSignature:
		//	Identifier TypeArgumentsopt
		//
		// ClassTypeSignatureSuffix:
		//	. SimpleClassTypeSignature
		//
		// The 'L' is consumed on entry to this function.
		//
		// ClassTypeSignatures can be nested, we need a local
		// builder for the descriptor.
		//
    int startPos = m_position - 1;
    boolean visited = false;
    boolean inner = false;
		do {
			if (m_char == '<') {
        String name = m_signature.substring(startPos, m_position - 1);
        if (inner) {
          v.visitInnerClassType(name);
        } else {
          v.visitClassType(name);
        }
        visited = true;
				read();
				typeArguments(v);
			} else {
        if (m_char == '.') {
          startPos = m_position;
          visited = false;
          inner = true;
        }
				read();
			}
		} while (m_char != ';');
		if (!visited) {
      String name = m_signature.substring(startPos, m_position - 1);
      if (inner) {
        v.visitInnerClassType(name);
      } else {
        v.visitClassType(name);
      }
    }
		if (m_position < m_length) {
			read();
    }
		v.visitEnd();
	}

	private void typeArguments (TypeSignatureVisitor v) { // can be inlined
		// TypeArguments:
		//	<TypeArgument+>
		//
		while (true) {
      char c = m_char;
			switch (c) {
				case '*':
					read();
					v.visitTypeArgument(c);
					break;
				case '+':
        case '-':
					read();
					fieldTypeSignature(v.visitTypeArgument(c));
					break;
				case '>':
					read();
					return;
				default:
					fieldTypeSignature(v.visitTypeArgument(' '));
					break;
			}
		}
	}

	private void typeVariableSignature (TypeSignatureVisitor v) { // can be inlined
		// TypeVariableSignature:
		//	T Identifer ;
		// The 'T' is consumed on entry.
		//
		int startPos = m_position - 1;
		do{
			read();
		} while (m_char != ';');
		v.visitTypeVariable(m_signature.substring(startPos, m_position - 1));    
		if (m_position < m_length) {
			read();
    }
	}

	private void typeSignature (TypeSignatureVisitor v) {
		// TypeSignature:
		//  FieldTypeSignature
		//  BaseType
		//
		switch (m_char) {
			case 'Z':
			case 'C':
			case 'B':
			case 'S':
			case 'I':
			case 'F':
			case 'J':
			case 'D':
      case 'V':
				v.visitBaseType(m_char);
				if (m_position < m_length) {
					read();
        }
				break;
			default:
				fieldTypeSignature(v);
		}
	}

	private void read () {
		m_char = m_signature.charAt(m_position++);
	}
}
