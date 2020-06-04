package org.example.hadoop.weblog.parsing;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;

public class LogParsing {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration configuration = new Configuration();
        String[] remainingArgs = new GenericOptionsParser(configuration, args).getRemainingArgs();

        if (remainingArgs.length != 2) {
            System.out.println("please enter the path of input and output dir.");
            System.exit(2);
        }

        Job job = new Job(configuration, LogParsing.class.getName());
        job.setJobName("Log Parsing");
        job.setJarByClass(LogParsing.class);
        job.setMapperClass(LogMapper.class);
        job.setNumReduceTasks(0);

        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        MultipleOutputs.addNamedOutput(job, "ParsedRecord", TextOutputFormat.class, NullWritable.class, Text.class);
        MultipleOutputs.addNamedOutput(job, "BadRecords", TextOutputFormat.class, NullWritable.class, Text.class);

        FileInputFormat.addInputPath(job, new Path(remainingArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(remainingArgs[1]));

        boolean complete = job.waitForCompletion(true);
        System.exit(complete ? 0 : 1);

    }
}
