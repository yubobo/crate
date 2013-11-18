package org.cratedb.sql.types;


public class ShortSQLType extends ScalarSQLType {

    public static final String NAME = "short";
    @Override
    public String mappingTypeName() {
        return NAME;
    }

    @Override
    protected boolean checkRange(Number value) {
        return Short.MIN_VALUE <= value.longValue() && value.longValue() <= Short.MAX_VALUE;
    }

    @Override
    protected Object convertNumber(Number value) throws ConvertException {
        return value.intValue();
    }


}
