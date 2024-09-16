package com.obbedcode.shared.db;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.helpers.StrBuilder;
import com.obbedcode.shared.utils.CollectionUtils;
import com.obbedcode.shared.xplex.data.XIdentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLQueryBuilder {
    public static final String VALUE_BIND = "?";

    public static final String OP_OR = "OR";
    public static final String OP_AND = "AND";

    public static final String BITWISE_EQUALS = "=";
    public static final String BITWISE_EQUAL_EQUAL = "==";
    public static final String BITWISE_VALUE_GREATER = "<";             //Less than
    public static final String BITWISE_VALUE_LESSER = ">";              //Greater than
    public static final String BITWISE_VALUE_GREATER_EQUAL = "<=";      //Less than Equal
    public static final String BITWISE_VALUE_LESSER_EQUAL = ">=";       //Greater than Equal
    public static final String BITWISE_NOT_EQUAL = "!=";
    public static final String BITWISE_NOT_EQUAL_S = "<>";

    public static final List<String> COMPARE_SYMBOLS = Arrays.asList(
            BITWISE_EQUALS,
            BITWISE_EQUAL_EQUAL,
            BITWISE_VALUE_GREATER,
            BITWISE_VALUE_LESSER,
            BITWISE_VALUE_GREATER_EQUAL,
            BITWISE_VALUE_LESSER_EQUAL,
            BITWISE_NOT_EQUAL,
            BITWISE_NOT_EQUAL_S);

    public static final List<String> OP_SYMBOLS = Arrays.asList(OP_OR, OP_AND);

    protected StringBuilder whereClause = new StringBuilder();
    protected List<String> whereArgs = new ArrayList<>();
    protected List<String> onlyReturn = new ArrayList<>();

    private int mColumnCount = 0;
    private String mOpSymbol  = OP_OR;
    private String mOpBitwise = BITWISE_EQUALS;
    public int getColumnCount() { return mColumnCount; }
    public String getOpSymbol() { return mOpSymbol; }
    public String getBitwise() { return mOpBitwise; }

    private String mTableName = null;
    public String getTableName() { return mTableName; }

    private String mColumnOrder = null;
    public String getColumnOrder() { return mColumnOrder; }

    private boolean mPushColumnIfNull = true;
    public boolean getPushColumnValueIfNull() { return mPushColumnIfNull; }

    public String[] getOnlyReturn() { return CollectionUtils.toStringArray(onlyReturn); }
    public String getWhereClause() { return whereClause.toString(); }
    public String[] getWhereArgs() { return CollectionUtils.toStringArray(whereArgs); }

    public SQLQueryBuilder() { }
    public SQLQueryBuilder(String tableName) { this.mTableName = tableName; }
    public SQLQueryBuilder(String tableName, boolean pushColumnIfNullValue) { this.mTableName = tableName; this.mPushColumnIfNull = pushColumnIfNullValue; }

    public SQLQueryBuilder whereColumns(String ... columns) {
        for(String c : columns) whereColumn(c, null);
        return this;
    }

    public SQLQueryBuilder whereValues(String ... values) {
        whereArgs.addAll(Arrays.asList(values));
        return this;
    }

    public SQLQueryBuilder whereIdentity(Integer userId, String category) {
        if(mPushColumnIfNull && userId == null) userId = XIdentity.GLOBAL_USER;
        if(mPushColumnIfNull && category == null) category = XIdentity.GLOBAL_NAMESPACE;
        whereColumn(XIdentity.FIELD_USER, userId, BITWISE_EQUALS, OP_AND);
        whereColumn(XIdentity.FIELD_CATEGORY, category, BITWISE_EQUALS, OP_AND);
        return this;
    }

    public SQLQueryBuilder whereColumn(String columnName, int value) { return whereColumn(columnName, value, null); }
    public SQLQueryBuilder whereColumn(String columnName, int value, String compareSymbol) { return whereColumn(columnName, value, compareSymbol, null); }
    public SQLQueryBuilder whereColumn(String columnName, int value, String compareSymbol, String logicalOp) { return whereColumn(columnName, value, compareSymbol, logicalOp, true); }
    public SQLQueryBuilder whereColumn(String columnName, int value, String compareSymbol, String logicalOp, boolean bindParams) { return whereColumn(columnName, String.valueOf(value), compareSymbol, logicalOp, bindParams); }

    //Make non null versions or something ?

    @SuppressWarnings("UnusedReturnValue")
    public SQLQueryBuilder whereColumn(String columnName, String value) { return whereColumn(columnName, value, null); }
    public SQLQueryBuilder whereColumn(String columnName, String value, String compareSymbol) { return whereColumn(columnName, value, compareSymbol, null); }
    public SQLQueryBuilder whereColumn(String columnName, String value, String compareSymbol, String logicalOp) {  return whereColumn(columnName, value, compareSymbol, logicalOp, true); }
    public SQLQueryBuilder whereColumn(String columnName, String value, String compareSymbol, String logicalOp, boolean bindParams) {
        if(!TextUtils.isEmpty(columnName) && (mPushColumnIfNull || value != null)) {
            String opUpper = logicalOp != null ? logicalOp.toUpperCase() : null;
            String opSym = opUpper == null || !OP_SYMBOLS.contains(opUpper) ? mOpSymbol : opUpper;
            if(mColumnCount != 0) whereClause.append(" ").append(opSym);        //Append [or] [and] if needed if more than one element

            whereClause.append(" ").append(columnName);                         //Append Column name like "CoolColumnName = ?"
            String sym = compareSymbol != null && COMPARE_SYMBOLS.contains(compareSymbol) ? compareSymbol : mOpSymbol;
            whereClause.append(" ").append(sym);                                //Append actual Bit wise op like [>] [<] [=]
            //We can bind to question mark (?) from Arg Value List (index based so first column has first value in value list)
            //We can also directly inline the value example "CoolColumn = 3"
            if(bindParams) {
                whereClause.append(" ").append(VALUE_BIND);
                //if(value == null) whereArgs.add("null");
                //else whereArgs.add(value);
                //whereArgs.add(value);
                if(value != null)
                    whereArgs.add(value);
            } else whereClause.append(" ").append(value);
            mColumnCount++;
        } return this;
    }

    public SQLQueryBuilder pushColumnValueIfNull(boolean pushIfNull) {
        this.mPushColumnIfNull = pushIfNull;
        return this;
    }

    public SQLQueryBuilder clearOnlyReturns() {
        this.onlyReturn.clear();
        return this;
    }

    public SQLQueryBuilder onlyReturn(String... columnNames) {
        if(columnNames != null) {
            for(String s : columnNames) {
                if(s == null) continue;
                s = s.trim();
                if(!TextUtils.isEmpty(s))
                    if(!onlyReturn.contains(s))
                        this.onlyReturn.add(s);
            }
        } return this;
    }

    public SQLQueryBuilder orderColumns(String orderFieldNameOrder) {
        this.mColumnOrder = orderFieldNameOrder;
        return this;
    }

    public SQLQueryBuilder bitwise(String bitwise) {
        if(COMPARE_SYMBOLS.contains(bitwise)) this.mOpBitwise = bitwise;
        return this;
    }

    public SQLQueryBuilder opSymbol(String symbol) {
        String up = symbol.toUpperCase();
        if(OP_SYMBOLS.contains(up)) this.mOpSymbol = up;
        return this;
    }

    public SQLQueryBuilder table(String tableName) {
        this.mTableName = tableName;
        return this;
    }

    public SQLQuerySnake asSnake() { return (SQLQuerySnake) this; }

    @NonNull
    @Override
    public String toString() {
        return StrBuilder.create()
                .appendFieldLine("Table Name", this.mTableName)
                .appendFieldLine("Where Clause", this.whereClause.toString())
                .appendFieldLine("Where Args", Str.joinList(this.whereArgs, ","))
                .appendFieldLine("Only Return", Str.joinList(this.onlyReturn, ","))
                .appendFieldLine("Order By", mColumnOrder)
                .toString();
    }
}
