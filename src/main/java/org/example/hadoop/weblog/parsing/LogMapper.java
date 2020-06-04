package org.example.hadoop.weblog.parsing;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogMapper extends Mapper<LongWritable, Text, NullWritable, Text> {


    private final String LOG_PATTERN = "^(\\S+) (\\S+) (\\S+) \\[(.+?)\\] \"([^\"]*)\" (\\S+) (\\S+) \"([^\"]*)\" \"([^\"]*)\"\"";

    private final int NUM_FIELDS = 9;
    private final String TAB_DELIMITER = "\t";
    private Pattern pattern;
    MultipleOutputs<NullWritable, Text> multipleOutputs = null;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        multipleOutputs = new MultipleOutputs<NullWritable, Text>(context);
        pattern = Pattern.compile(LOG_PATTERN);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        super.map(key, value, context);
        String formattedValue = value.toString().replaceAll("\t", " ").trim();
        Matcher matcher = pattern.matcher(formattedValue);
        boolean matches = matcher.matches();

        if(matches && matcher.groupCount() == NUM_FIELDS) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(matcher.group(1)).append(TAB_DELIMITER);
            stringBuffer.append(matcher.group(2)).append(TAB_DELIMITER);
            stringBuffer.append(matcher.group(3)).append(TAB_DELIMITER);
            stringBuffer.append(matcher.group(4)).append(TAB_DELIMITER);

            multipleOutputs.write("ParsedRecord", NullWritable.get(), new Text(stringBuffer.toString()));
        } else {
            multipleOutputs.write("BadRecords", NullWritable.get(), value);
        }


    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        super.cleanup(context);
        multipleOutputs.close();
    }
}
