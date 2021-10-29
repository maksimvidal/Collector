package com.softserve.logstat.collectors;

import java.util.ArrayList;
import java.util.List;

import com.softserve.logstat.model.Log;

import formater.TableFormatter;

public class Test {

 	static String command="-top 3 -size -url where -size :ne 1 and -size :ne 3 sortby -size";
 	
	public static void main(String[] args) {
		List<Log> logs=new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Log log=new Log();
			log.setResponseSize(10-i);
			logs.add(log);
		}
		
		
		TopCollectorArgParser parser=new TopCollectorArgParser();
		TopCollector collector=new TopCollector();
		TableFormatter formatter=new TableFormatter();
		System.out.println(collector.getFieldByParam(ParamType.SIZE, logs.get(0)));
		var result=collector.execute(parser.parseCommand(command), logs);
		formatter.draw(result, parser.parseCommand(command));
	}
}
