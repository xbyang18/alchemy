package com.dfire.platform.alchemy.connector.tsdb;

import com.dfire.platform.alchemy.client.request.SqlSubmitFlinkRequest;
import com.dfire.platform.alchemy.client.response.Response;
import com.dfire.platform.alchemy.client.response.SubmitFlinkResponse;
import com.dfire.platform.alchemy.connector.BaseConnectorTest;
import com.dfire.platform.alchemy.descriptor.SinkDescriptor;
import com.dfire.platform.alchemy.descriptor.SourceDescriptor;
import com.dfire.platform.alchemy.domain.enumeration.SinkType;
import com.dfire.platform.alchemy.domain.enumeration.SourceType;
import com.dfire.platform.alchemy.domain.enumeration.TableType;
import com.dfire.platform.alchemy.service.util.SqlParseUtil;
import com.dfire.platform.alchemy.util.BindPropertiesUtil;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;

/**
 * @author congbai
 * @date 2019/5/29
 */
public class TsdbTest extends BaseConnectorTest {

    @Test
    public void write() throws Exception {
        Response response = execute("insert into tsdb_sink select * from csv_source");
        assert response.isSuccess();
    }

    Response execute(String sql) throws Exception {
        File sqlJobFile = ResourceUtils.getFile("classpath:yaml/sql.yaml");
        SqlSubmitFlinkRequest sqlSubmitFlinkRequest
            = BindPropertiesUtil.bindProperties(sqlJobFile, SqlSubmitFlinkRequest.class);
        SourceDescriptor sourceDescriptor = createSource("csv_source", "classpath:yaml/csv-source.yaml", SourceType.CSV, TableType.TABLE);
        SinkDescriptor sinkDescriptor = createSink("tsdb_sink", "classpath:yaml/tsdb-sink.yaml", SinkType.TSDB);
        sqlSubmitFlinkRequest.setSources(Lists.newArrayList(sourceDescriptor));
        sqlSubmitFlinkRequest.setSinks(Lists.newArrayList(sinkDescriptor));
        sqlSubmitFlinkRequest.setSqls(SqlParseUtil.findQuerySql(Lists.newArrayList(sql)));
        client.submit(sqlSubmitFlinkRequest, (SubmitFlinkResponse response) -> {
            }
        );
        return new Response(true);
    }

}
