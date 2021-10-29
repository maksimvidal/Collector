package com.softserve.logstat.collectors;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.softserve.logstat.model.Log;

public class TopCollector implements Collector {

	@Override
	public List<String> execute(Command command, List<Log> logs) {
		Stream<Log> tmp = logs.stream().filter(command.getConditions().stream().reduce(x -> true, Predicate::and));

		return sort(tmp, command.getComparator()).limit(command.getLimit())
				.flatMap(x -> Stream.of(buildAnswer(x, command.getToWrite()))).collect(Collectors.toList());
	}

	protected Stream<Log> sort(Stream<Log> logs, Comparator<Log> comparator) {
		if (comparator != null)	
		  return logs.sorted(comparator);
	return logs;
	}

	protected String buildAnswer(final Log log, List<ParamType> toWrite) {
		StringBuilder result = new StringBuilder();

		toWrite.stream().forEach(x -> result.append(getFieldByParam(x, log) + ";"));
		return result.toString();
	}

	protected Object getFieldByParam(ParamType type, Log log) {
		Object val = null;
		val=switch (type) {
		case URL-> log.getRequest();
		case IP->log.getIp();
		case SIZE-> log.getResponseSize();
		case SC->  log.getResponseCode();
		case DATETIME -> log.getDateTime();
		case AGENT -> log.getUserAgent();
		case HTTPVERSION-> log.getHttpVersion();
		case METHOD-> log.getMethod();
		case REFFERER-> log.getReferrer();
		default->null;
		};
		return val;
	}
}
