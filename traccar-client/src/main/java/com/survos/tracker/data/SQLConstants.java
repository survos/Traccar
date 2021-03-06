
package com.survos.tracker.data;

/**
 * Constant interface to hold SQL Strings. Do NOT implement.
 * 
 */
public interface SQLConstants {
    public static final String CREATE_TABLE           = "CREATE TABLE IF NOT EXISTS %s (%s);";
    public static final String CREATE_VIEW            = "CREATE VIEW IF NOT EXISTS %s AS %s;";
    public static final String SELECT_FROM_WHERE      = "SELECT %s FROM %s WHERE %s";
    public static final String SELECT_DISCTINT_FROM_WHERE      = "SELECT DISTINCT %s FROM %s WHERE %s";
    public static final String SELECT_FROM            = "SELECT %s FROM %s";
    public static final String INSERT                 = "INSERT INTO %s (%s) %s";
    public static final String UPDATE                 = "UPDATE %s SET %s WHERE %s";
    public static final String DELETE_FROM_WHERE      = "DELETE FROM %s WHERE %s";
    public static final String ALTER_TABLE_ADD_COLUMN = "ALTER TABLE %s ADD COLUMN %s;";
    public static final String TABLE_ALIAS            = "%s %s";
    public static final String ALIAS_COLUMN           = "%s.%s";
    public static final String DROP_TABLE_IF_EXISTS   = "DROP TABLE IF EXISTS %s;";
    public static final String DROP_VIEW_IF_EXISTS    = "DROP VIEW IF EXISTS %s;";
    public static final String DATA_TEXT              = "%s TEXT DEFAULT '%s' ";
    public static final String DATA_INTEGER           = "%s INTEGER DEFAULT %d ";
    public static final String DATA_REAL              = "%s REAL DEFAULT %f ";
    public static final String DATA_INTEGER_PK        = "%s INTEGER PRIMARY KEY AUTOINCREMENT ";
    public static final String ASCENDING              = " ASC ";
    public static final String DESCENDING             = " DESC ";
    public static final String LIKE_ARG               = " LIKE ?";
    public static final String AND                    = " AND ";
    public static final String OR                     = " OR ";
    public static final String EQUALS_ARG             = "=?";
    public static final String NOT_EQUALS_ARG         = "!=?";
    public static final String EQUALS_QUOTE           = "='";
    public static final String FROM                   = " FROM ";
    public static final String WHERE                  = " WHERE ";
    public static final String IN                     = " IN ";
    public static final String SELECT                 = " SELECT ";
    public static final String GROUP_BY               = "GROUP_BY ";
    public static final String HAVING                 = "HAVING";
    public static final String LIMIT                  = "LIMIT";
    public static final String DISTINCT               = "DISTINCT";
    public static final String PARANTHESIS_OPEN       = " ( ";
    public static final String PARANTHESES_CLOSE      = " ) ";
    public static final String LESS_THAN              = "<";
    public static final String GREATER_THAN           = ">";
    public static final String EQUALS                 = "=";
    public static final String COMMA                  = ",";
    public static final String SLASH                  = "/";
    public static final String STAR                   = "*";
    public static final String PERCENT                = "%";
    public static final String DQUOTE                 = "\"";
    public static final String QUOTE                  = "'";
    public static final String SEMI_COLON             = ";";
    public static final String STRING_SLASH           = "\\";
}
