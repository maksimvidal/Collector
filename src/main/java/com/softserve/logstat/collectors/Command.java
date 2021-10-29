package com.softserve.logstat.collectors;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.softserve.logstat.model.Log;

public class Command {

	private List<Predicate<Log>> conditions;
	private List<ParamType> toWrite;
	private int limit;
	private Comparator<Log> comparator;
	
	protected List<Predicate<Log>> getConditions() {
		return conditions;
	}
	protected void setConditions(List<Predicate<Log>> conditions) {
		this.conditions = conditions;
	}
	protected List<ParamType> getToWrite() {
		return toWrite;
	}
	protected void setToWrite(List<ParamType> toWrite) {
		this.toWrite = toWrite;
	}
	protected int getLimit() {
		return limit;
	}
	protected void setLimit(int limit) {
		this.limit = limit;
	}
	protected Comparator<Log> getComparator() {
		return comparator;
	}
	protected void setComparator(Comparator<Log> comparator) {
		this.comparator = comparator;
	}
	
	public String[] getParamArray() {
		return toWrite.
				stream().
				flatMap(x->Stream.of(x.toString())).
				toArray(String[]::new);
	}
	
	@Override
	public String toString() {
		return "Command [conditions=" + conditions + ", toWrite=" + toWrite + ", limit=" + limit + ", comparator="
				+ comparator + "]";
	}
	
}
