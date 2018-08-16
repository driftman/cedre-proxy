/*
 *  Copyright 2017 predic8 GmbH, www.predic8.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.predic8.membrane.core.interceptor.prometheus;

import com.predic8.membrane.annot.MCElement;
import com.predic8.membrane.core.exchange.Exchange;
import com.predic8.membrane.core.http.Header;
import com.predic8.membrane.core.http.MimeType;
import com.predic8.membrane.core.http.Response;
import com.predic8.membrane.core.interceptor.AbstractInterceptor;
import com.predic8.membrane.core.interceptor.Outcome;
import com.predic8.membrane.core.rules.Rule;
import com.predic8.membrane.core.rules.StatisticCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@MCElement(name = "prometheus")
public class PrometheusInterceptor extends AbstractInterceptor {

    static final Logger LOG = LoggerFactory.getLogger(PrometheusInterceptor.class);
    static volatile boolean issuedDuplicateRuleNameWarning = false;

    @Override
    public Outcome handleRequest(Exchange exc) throws Exception {
        Context ctx = new Context();
        buildPrometheusStyleResponse(ctx);
        exc.setResponse(Response.ok(ctx.sb.toString()).header(Header.CONTENT_TYPE, "text/plain; version=0.0.4").build());
        return Outcome.RETURN;
    }

    private class Context {
        StringBuilder sb = new StringBuilder();

        StringBuilder s1 = new StringBuilder();
        StringBuilder s2 = new StringBuilder();
        StringBuilder s3 = new StringBuilder();
        StringBuilder s4 = new StringBuilder();
        StringBuilder s5 = new StringBuilder();

        HashSet<String> seenRules = new HashSet<>();

        private void reset() {
            s1.setLength(0);
            s2.setLength(0);
            s3.setLength(0);
            s4.setLength(0);
            s5.setLength(0);
        }

        private void resetAll() {
            sb.setLength(0);
            seenRules.clear();
        }

        private void collect() {
            sb.append(s1);
            sb.append(s2);
            sb.append(s3);
            sb.append(s4);
            sb.append(s5);
        }
    }

    private void buildPrometheusStyleResponse(Context ctx) {
        ctx.resetAll();
        ctx.reset();
        for (Rule r : router.getRuleManager().getRules()) {
            if (!ctx.seenRules.add(prometheusCompatibleName(r.getName()))) {
                // the prometheus format is not allowed to contain the same metric more than once
                if (issuedDuplicateRuleNameWarning)
                    continue;
                LOG.warn("The prometheus interceptor detected the same rule name more than once.");
                issuedDuplicateRuleNameWarning = true;
                continue;
            }

            buildStatuscodeLines(ctx, r);
        }
        ctx.collect();

    }

    private void buildStatuscodeLines(Context ctx, Rule rule) {
        Map<Integer, StatisticCollector> stats = rule.getStatisticCollector().getStatisticsByStatusCodes();

        for (Integer code : stats.keySet()) {
            buildLine(ctx.s1, rule.getName(), stats.get(code).getCount(), "code", code, "count");
            buildLine(ctx.s2, rule.getName(), stats.get(code).getGoodCount(), "code", code, "good_count");
            buildLine(ctx.s3, rule.getName(), stats.get(code).getGoodTotalTime(), "code", code, "good_time");
            buildLine(ctx.s4, rule.getName(), stats.get(code).getGoodTotalBytesSent(), "code", code, "good_bytes_req_body");
            buildLine(ctx.s5, rule.getName(), stats.get(code).getGoodTotalBytesReceived(), "code", code, "good_bytes_res_body");
        }

    }

    /**
     * see https://prometheus.io/docs/instrumenting/exposition_formats/ .
     */
    private StringBuilder buildLine(StringBuilder sb, String ruleName, long value, String labelName, int labelValue, String postFix) {
        String prometheusName = prometheusCompatibleName("membrane_" + postFix);
        if (sb.length() == 0) {
            sb.append("# TYPE " + prometheusName + " counter\n");
        }
        sb.append(prometheusName);
        sb.append("{rule=\"");
        sb.append(prometheusCompatibleName(ruleName));
        sb.append("\",");
        sb.append(prometheusCompatibleName(labelName));
        sb.append("=\"");
        sb.append(labelValue);
        sb.append("\"} ");
        sb.append(value);
        sb.append("\n");
        return sb;
    }

    ConcurrentHashMap<String, String> names = new ConcurrentHashMap<>();
    // see https://prometheus.io/docs/concepts/data_model/
    Pattern ILLEGAL_FIRST_CHAR = Pattern.compile("^[^a-zA-Z_:]");
    Pattern ILLEGAL_CHARS = Pattern.compile("[^a-zA-Z0-9_:]");

    private String prometheusCompatibleName(String name) {
        String result = names.get(name);
        if (result != null)
            return result;

        result = prettyPrint(name);

        result = ILLEGAL_CHARS.matcher(result).replaceAll("_");
        result = ILLEGAL_FIRST_CHAR.matcher(result).replaceAll("_");
        names.put(name, result);

        return result;
    }

    private String prettyPrint(String ruleName) {
        StringBuilder sb = new StringBuilder();
        String[] split = ruleName.toLowerCase().split(" ");
        for (String s : split)
            sb.append(s).append("_");
        sb.setLength(sb.length() - 1);
        return sb.toString();

    }
}
