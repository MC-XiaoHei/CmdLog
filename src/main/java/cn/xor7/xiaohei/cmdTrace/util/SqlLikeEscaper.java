package cn.xor7.xiaohei.cmdTrace.util;

public final class SqlLikeEscaper {

    private SqlLikeEscaper() {
    }

    public static String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
