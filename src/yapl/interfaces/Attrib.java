package yapl.interfaces;

import yapl.lib.Type;

/**
 * Attributes of production symbols for type checking and code generation.
 * An object implementing this interface stores attributes of production
 * symbols of the YAPL grammar. Conceptually, an Attrib object represents an
 * operand of a generated instruction, or a branch condition.
 * <p>
 * The interface constants specify the various kinds of operands
 * represented by an object implementing this interface. Operand kinds comprise
 * registers, constants, memory operands, array elements, and record fields.
 * The latter two kinds are only needed for target languages that require special
 * instructions to access array elements and record fields.
 * </p><p>
 * The term <em>register</em> is used for both register and stack machines.
 * In the latter case, a register value refers to an element
 * on the expression stack; register numbers are not needed then, because
 * the order of operands is implied by the stack.
 * </p>
 * 
 * @author Mario Taschwer
 * @version $Id$
 */
public interface Attrib {
	/** Invalid operand. */
	public static final byte Invalid = 0;
	
	/** Register or stack operand value. Requires a data type and 
	 * (if applicable) a register number. 
	 */
	public static final byte RegValue  = 1;
	
	/** Register or stack operand address. Requires a data type and 
	 * (if applicable) a register number. 
	 */
	public static final byte RegAddress  = 2;
	
	/** Constant operand. Requires a data type. */
	public static final byte Constant  = 3;
	
	/** Memory operand. 
	 * Requires a data type and a memory address.
	 */
	public static final byte MemoryOperand   = 4;
	
	public static final byte ArrayElement = 5;
	
	public static final byte RecordField = 6;
	
	/** Return the kind of operand represented by this object.
	 * 
	 * @return one of the constants defined by this interface.
	 */
	public byte getKind();
	
	/**
	 * Set the kind of operand represented by this object.
	 * 
	 * @param kind     one of the constants defined by this interface.
	 */
	public void setKind(byte kind);
	
	/** Get the data type of this operand (or its target object). */
	public Type getType();
	
	/** Set the data type of this operand (or its target object). */
	public void setType(Type type);
	
	/** Return <code>true</code> iff this operand can be evaluated
	 * at compile time.
	 */
	public boolean isConstant();
	
	/** Specify whether this operand can be evaluated at compile time. */
	public void setConstant(boolean isConstant);
	
	/** Return <code>true</code> iff this operand must not be modified. */
	public boolean isReadonly();
	
	/** Specify whether this operand must not be modified. */
	public void setReadonly(boolean isReadonly);
	
	/** Return <code>true</code> iff this operand represents
	 * a global memory object (i.e. it is stored in heap memory).
	 */
	public boolean isGlobal();
	
	/** Specify whether this operand represents a global memory object.
	 * @see #isGlobal()
	 */
	public void setGlobal(boolean isGlobal);
	
	/** Return the address offset (for memory operands).
	 *  If this operand represents a global memory object
	 *  (see {@link #isGlobal()}), the offset is relative to
	 *  the global variable storage area.
	 *  If this operand represents a record field, the offset
	 *  is relative to the record start address. 
	 *  Otherwise, the offset is relative to the current stack frame.
	 */
	public int getOffset();
	
	/** Set the address offset. 
	 * @see #getOffset()
	 */
	public void setOffset(int offset);
	
	/** Get the register number (for register operands). */
	public byte getRegister();
	
	/** Set the register number (for register operands). */
	public void setRegister(byte register);
	
}
