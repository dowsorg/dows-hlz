package org.dows.hep.biz.snapshot;

import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import org.dows.hep.biz.util.ShareUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/13 20:05
 */
public class SnapshotReadInterceptor extends DynamicTableNameInnerInterceptor {


    protected static final ThreadLocal<EnumSnapshotType> localSnapshotType=new ThreadLocal<>();
    protected static final ThreadLocal<String> localSqlKey=new ThreadLocal<>();
    @Override
    protected String changeTable(String sql) {
        return buildSnapshotSql(rawChangeTable(sql));
    }
    protected String buildSnapshotSql(String sql) {
        EnumSnapshotType snapshotType = localSnapshotType.get();
        if (null == snapshotType) {
            localSqlKey.remove();
            return sql;
        }
        final String sqlKey=getSqlKey(sql);
        int orig=sql.indexOf(snapshotType.getDstTableName());
        if(orig<0||!ShareUtil.XObject.nullSafeEquals(sqlKey, localSqlKey.get())){
            return sql;
        }
        try {
            String experimentId = SnapshotRequestHolder.getRefExperimentId(snapshotType);
            experimentId=ShareUtil.XString.defaultIfEmpty(experimentId, "");
            int start = sql.toLowerCase().indexOf("where",orig);
            String eqExperimentId = String.format(" %s='%s' ", snapshotType.getColExperimentInstanceId() ,experimentId);
            StringBuilder sb = new StringBuilder();
            if (start < 0) {
                int last=orig+snapshotType.getDstTableName().length();
                sb.append(sql.substring(0, last));
                sb.append(" where ");
                sb.append(eqExperimentId);
                sb.append(sql.substring(last));
            } else {
                int last = start + 5;
                sb.append(sql.substring(0, last));
                sb.append(eqExperimentId);
                sb.append(" and ");
                sb.append(sql.substring(last));
            }
            return sb.toString();
        } finally {
            localSqlKey.remove();
            localSnapshotType.remove();
        }
    }
    protected String rawChangeTable(String sql) {
        TableNameParser parser = new TableNameParser(sql);
        List<TableNameParser.SqlToken> names = new ArrayList<>();
        parser.accept(names::add);
        StringBuilder builder = new StringBuilder();
        int last = 0;
        boolean changed=false;
        for (TableNameParser.SqlToken name : names) {
            int start = name.getStart();
            if (start != last) {
                builder.append(sql, last, start);
                String newTableName=getSnapshotTableName(sql,name.getValue());
                builder.append(newTableName);
                if(!changed){
                    changed=!newTableName.equals(name.getValue());
                }
            }
            last = name.getEnd();
        }
        if (last != sql.length()) {
            builder.append(sql.substring(last));
        }
        if (this.getHook() != null) {
            this.getHook().run();
        }
        String rst=builder.toString();
        if(changed){
            localSqlKey.set(getSqlKey(rst));
        }
        return rst;
    }

    String getSnapshotTableName(String sql, String tableName) {
        EnumSnapshotType snapshotType = EnumSnapshotType.ofSrcTableName(tableName);
        if (snapshotType == EnumSnapshotType.NONE) {
            return tableName;
        }
        if (!SnapshotRequestHolder.hasSnapshotRequest()) {
            return tableName;
        }
        localSnapshotType.set(snapshotType);
        return snapshotType.getDstTableName();
    }
    String getSqlKey(String sql){
        return String.format("%s-%s", sql.length(),sql.hashCode());
    }


}
