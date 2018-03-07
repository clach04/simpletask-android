/*
 * ExprValue.java
 *
 * Copyright (c) 1997 Cornell University.
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * RCS: @(#) $Id: ExprValue.java,v 1.12 2006/06/27 21:14:42 mdejong Exp $
 *
 */

package tcl.lang;

/**
 * Describes an expression value, which can be either an integer (the usual
 * case), a double-precision floating-point value, or a string. A number type
 * will typically have a string value that is the number string before it was
 * parsed into a number. If the number has no string value then one will be
 * generated by getStringValue().
 */

public final class ExprValue {
	public static final int INT = 0;
	public static final int DOUBLE = 1;
	public static final int STRING = 2;

	/**
	 * Integer value, if any.
	 */
	private long intValue;

	/**
	 * Floating-point value, if any.
	 */
	private double doubleValue;

	/**
	 * Used to hold a string value, if any.
	 */
	private String stringValue;

	/**
	 * Type of value: INT, DOUBLE, or STRING.
	 */
	private int type;

	/**
	 * Extra debug checking
	 */
	private static final boolean validate = false;

	public ExprValue(long i, String s) {
		setIntValue(i, s);
	}

	public ExprValue(double d, String s) {
		setDoubleValue(d, s);
	}

	public ExprValue(String s) {
		setStringValue(s);
	}

	public ExprValue(boolean b) {
		setIntValue(b);
	}

	public final int getType() {
		return type;
	}

	public final boolean isIntType() {
		return type == INT;
	}

	public final boolean isDoubleType() {
		return type == DOUBLE;
	}

	public final boolean isStringType() {
		return type == STRING;
	}

	public final boolean isIntOrDoubleType() {
		return (type == INT) || (type == DOUBLE);
	}

	public final long getIntValue() {
		if (validate) {
			if (type != INT) {
				throw new TclRuntimeError(
						"called getIntValue() on non-INT type");
			}
		}
		return intValue;
	}

	public final double getDoubleValue() {
		if (validate) {
			if (type != DOUBLE) {
				throw new TclRuntimeError(
						"called getDoubleValue() on non-DOUBLE type");
			}
		}
		return doubleValue;
	}

	public final String getStringValue() {
		if (type == STRING) {
			// No-op
		} else if (type == INT) {
			if (stringValue == null) {
				stringValue = Long.toString(intValue);
			}
		} else if (type == DOUBLE) {
			if (stringValue == null) {
				// Generate Tcl string rep for the double.
				stringValue = Util.printDouble(doubleValue);
			}
		}
		return stringValue;
	}

	public final boolean getBooleanValue(Interp interp) throws TclException // Raise
																			// TclException
																			// if
																			// string
																			// is
																			// not
																			// a
																			// boolean
	{
		switch (type) {
		case ExprValue.INT:
			return (intValue != 0);
		case ExprValue.DOUBLE:
			return (doubleValue != 0.0);
		case ExprValue.STRING:
			return Util.getBoolean(interp, stringValue);
		default:
			throw new TclRuntimeError("internal error: expression, unknown");
		}
	}

	public final void setIntValue(long value) {
		stringValue = null;
		intValue = value;
		type = INT;
	}

	public final void setIntValue(long value, String s) {
		stringValue = s;
		intValue = value;
		type = INT;
	}

	public final void setIntValue(boolean b) {
		stringValue = null;
		intValue = (b ? 1 : 0);
		type = INT;
	}

	public final void setDoubleValue(double value) {
		stringValue = null;
		doubleValue = value;
		type = DOUBLE;
	}

	public final void setDoubleValue(double value, String s) {
		stringValue = s;
		doubleValue = value;
		type = DOUBLE;
	}

	public final void setStringValue(String s) {
		if (s == null) {
			throw new NullPointerException();
		}
		stringValue = s;
		type = STRING;
	}

	public final void setValue(ExprValue value2) {
		// Copy bits over, including any logic based
		// on type that causes branching make this
		// method execute 2x slower.
		type = value2.type;
		intValue = value2.intValue;
		doubleValue = value2.doubleValue;
		stringValue = value2.stringValue;
	}

	public final void nullStringValue() {
		stringValue = null;
	}

	final void toStringType() {
		if (type == STRING) {
			throw new TclRuntimeError("called toStringType() on STRING type");
		}
		if (stringValue == null) {
			getStringValue();
		}
		type = STRING;
	}

	// This method is used only for debugging, it prints a description
	// of the internal state of a ExprValue object.

	public String toString() {
		StringBuilder sb = new StringBuilder(64);
		if (type == STRING) {
			sb.append("STRING \"" + stringValue + "\"");
		} else if (type == INT) {
			sb.append("INT \"" + intValue + "\"");
			if (stringValue != null) {
				String intString = Long.toString(intValue);
				if (intString.compareTo(stringValue) != 0) {
					sb.append(" parsed from \"");
					sb.append(stringValue);
					sb.append("\"");
				}
			}
		} else if (type == DOUBLE) {
			sb.append("DOUBLE \"" + doubleValue + "\"");
			if (stringValue != null) {
				String doubleString = Util.printDouble(doubleValue);
				if (doubleString.compareTo(stringValue) != 0) {
					sb.append(" parsed from \"");
					sb.append(stringValue);
					sb.append("\"");
				}
			}
		}
		return sb.toString();
	}

	// Optimized int multiply operation

	final void optIntMult(final ExprValue value2) {
		stringValue = null;
		intValue *= value2.intValue;
	}

	// Optimized double multiply operation

	final void optDoubleMult(final ExprValue value2) {
		stringValue = null;
		doubleValue *= value2.doubleValue;
	}

	// Optimized int plus operation

	public final void optIntPlus(final ExprValue value2) {
		stringValue = null;
		intValue += value2.intValue;
	}

	// Optimized double plus operation

	final void optDoublePlus(final ExprValue value2) {
		stringValue = null;
		doubleValue += value2.doubleValue;
	}

	// Optimized int minus operation

	final void optIntMinus(final ExprValue value2) {
		stringValue = null;
		intValue -= value2.intValue;
	}

	// Optimized double minus operation

	final void optDoubleMinus(final ExprValue value2) {
		stringValue = null;
		doubleValue -= value2.doubleValue;
	}

	// Optimized int less than operation

	final void optIntLess(final ExprValue value2) {
		stringValue = null;
		intValue = ((intValue < value2.intValue) ? 1 : 0);
	}

	// Optimized double less than operation

	final void optDoubleLess(final ExprValue value2) {
		stringValue = null;
		intValue = ((doubleValue < value2.doubleValue) ? 1 : 0);
		type = INT;
	}

	// Optimized int greater than operation

	final void optIntGreater(final ExprValue value2) {
		stringValue = null;
		intValue = ((intValue > value2.intValue) ? 1 : 0);
	}

	// Optimized double greater than operation

	final void optDoubleGreater(final ExprValue value2) {
		stringValue = null;
		intValue = ((doubleValue > value2.doubleValue) ? 1 : 0);
		type = INT;
	}

	// Optimized int less than or equal to operation

	final void optIntLessEq(final ExprValue value2) {
		stringValue = null;
		intValue = ((intValue <= value2.intValue) ? 1 : 0);
	}

	// Optimized int less than or equal to operation

	final void optDoubleLessEq(final ExprValue value2) {
		stringValue = null;
		intValue = ((doubleValue <= value2.doubleValue) ? 1 : 0);
		type = INT;
	}

	// Optimized int greater than or equal to operation

	final void optIntGreaterEq(final ExprValue value2) {
		stringValue = null;
		intValue = ((intValue >= value2.intValue) ? 1 : 0);
	}

	// Optimized double greater than or equal to operation

	final void optDoubleGreaterEq(final ExprValue value2) {
		stringValue = null;
		intValue = ((doubleValue >= value2.doubleValue) ? 1 : 0);
		type = INT;
	}

	// Optimized int equal to operation

	final void optIntEq(final ExprValue value2) {
		stringValue = null;
		intValue = ((intValue == value2.intValue) ? 1 : 0);
	}

	// Optimized double equal to operation

	final void optDoubleEq(final ExprValue value2) {
		stringValue = null;
		intValue = ((doubleValue == value2.doubleValue) ? 1 : 0);
		type = INT;
	}

	// Optimized int not equal to operation

	final void optIntNotEq(final ExprValue value2) {
		stringValue = null;
		intValue = ((intValue != value2.intValue) ? 1 : 0);
	}

	// Optimized double not equal to operation

	final void optDoubleNotEq(final ExprValue value2) {
		stringValue = null;
		intValue = ((doubleValue != value2.doubleValue) ? 1 : 0);
		type = INT;
	}

	// Optimized integer unary logical not operation '!'

	public final void optIntUnaryNot() {
		if (validate) {
			if (type != INT) {
				throw new TclRuntimeError(
						"called optIntUnaryNot() on non-INT type");
			}
		}

		stringValue = null;
		intValue = (intValue == 0) ? 1 : 0;
	}

	public final void optIntUnaryNotNstr() {
		if (validate) {
			if (type != INT) {
				throw new TclRuntimeError(
						"called optIntUnaryNotNstr() on non-INT type");
			}
			if (stringValue != null) {
				throw new TclRuntimeError(
						"called optIntUnaryNotNstr() with non-null string value");
			}
		}

		intValue = (intValue == 0) ? 1 : 0;
	}

}
