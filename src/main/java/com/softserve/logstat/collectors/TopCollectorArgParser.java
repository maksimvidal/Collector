package com.softserve.logstat.collectors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.softserve.logstat.model.HTTPMethod;
import com.softserve.logstat.model.Log;

public class TopCollectorArgParser {

	String command="-top 44 -size -url where -size != 1 and -size != 3 sortby -size";
 	Map<String,Optional<String>> commandInfo=new LinkedHashMap<>();
 	final List<String> keyWords= new ArrayList<>();
 	
 	{
 		commandInfo.put("parameters", Optional.empty());
 		commandInfo.put("filters", Optional.empty());
 		commandInfo.put("sortby", Optional.empty());
 		commandInfo.put("limit", Optional.empty());
 		
 		keyWords.add("where");
 		keyWords.add("sortby");
 		keyWords.add("EOF");
 		
 	}
 	
 	public Command parseCommand(String command) {
 		Command parsedCommand=new Command();
 		defineCommandInfo(command);
 		
 		String[] tmp=commandInfo.get("parameters").orElse("").split(" ");
 		parsedCommand.setToWrite(getToWriteParams(tmp));
 		
 		tmp=commandInfo.get("filters").orElse("").split(" ");
 		parsedCommand.setConditions(getCondtitions(tmp));
 		
 		parsedCommand.setLimit(getLimit());
 		
 		String sortedParam=commandInfo.get("sortby").orElse("");
 		parsedCommand.setComparator(getComparator(sortedParam));
 		
 		return parsedCommand;
 	}
 	
	void defineCommandInfo(String command) {
		int to=command.indexOf(" ",5);
		String limit=command.substring(5, to);
		commandInfo.put("limit", Optional.of(limit));
		int from=to+1;
		
		 var infoIt=commandInfo.keySet().iterator();
		 var wordIt=keyWords.iterator();
		
		while(infoIt.hasNext() && wordIt.hasNext()) {
			String keyWord=wordIt.next();
			to=findIndex(command, keyWord);
			commandInfo.put(infoIt.next(), Optional.of(command.substring(from,to)));
			from=to+keyWord.length()+1;
			if(to >= command.length())
				break;
		}
	}
	
	 protected int findIndex(String line,String key) {
		int index=line.indexOf(key);
		if(index == -1)
			return line.length();
		return index;
	}
	 
	 protected List<ParamType> getToWriteParams(String[] params) {
			List<ParamType> toWrite=new ArrayList<>();
			for(String param:params) {
				toWrite.add(getParamType(param));
			}
			return toWrite;
		}
	 
	 protected ParamType getParamType(String type) {
			ParamType typeToWrite=null;
			typeToWrite=switch (type) {
				case "-size"->ParamType.SIZE;
				case "-url"->ParamType.URL;
				case "-ip"->ParamType.IP;
				case "-sc"->ParamType.SC;
				case "-httpmethod"->ParamType.METHOD;
				case "-agent"->ParamType.AGENT;
				case "-httpversion"->ParamType.HTTPVERSION;
				case "-datetime"->ParamType.DATETIME;
				case "-refferer"->ParamType.REFFERER;
				default->null;
			};
			return typeToWrite;
		}
	 
	 protected List<Predicate<Log>> getCondtitions(String[] conditions) {
			List<Predicate<Log>> filters=new ArrayList<>();
			for(int i=0;i<conditions.length;i+=3) {
				if(conditions[i].equals("and") && i+1<conditions.length)
					i++;
				filters.add(getFilter(conditions[i], conditions[i+1], conditions[i+2]));
			}
			return filters;
		}
		
		protected Predicate<Log> getFilter(String field,String operator,String value) {
			Predicate<Log> operation=null;
			operation=switch(operator) {
			case ":e"->x->getFieldValue(field,x).compareTo(parseTo(value, field)) == 0;
			case ":mt"->x->getFieldValue(field,x).compareTo(parseTo(value, field)) > 0;
			case ":lt"->x->getFieldValue(field,x).compareTo(parseTo(value, field)) < 0;
			case ":ne"->x->getFieldValue(field,x).compareTo(parseTo(value, field)) != 0;
			default->null;
			};
			return operation;
		}
		
		protected Comparable<?> getFieldValue(String fieldName,Log log){
			Comparable<?> field=null;
			field=switch (fieldName) {
			case "-url"->log.getRequest();
			case "-ip"->log.getIp();
			case "-size"->log.getResponseSize();
			case "-sc"->log.getResponseCode();
			case "-httpmethod"->log.getMethod();
			case "-httpversion"->log.getHttpVersion();
			case "-agent"->log.getUserAgent();
			case "-refferer"->log.getReferrer();
			case "-datetime"->log.getDateTime();
			default->null;
		   };
			return field;
		}
		
		@SuppressWarnings("unchecked")
		protected<T> T parseTo(String value,String fieldName){
			Object val=null;
			val=switch (fieldName) {
			case "-size"->Integer.valueOf(value);
			case "-httpmethod"->HTTPMethod.valueOf(value);
			case "-sc"->Short.valueOf(value);
			case "-datetime"->LocalDateTime.parse(value);
			default->null;
		   };
			return (T)val;
		}
		
		protected int getLimit() {
			return Integer.parseInt(commandInfo.get("limit").orElse("0"));
		} 
		
		protected Comparator<Log> getComparator(String sortedParam){
			ParamType type=getParamType(sortedParam.trim());
			Comparator<Log> comparator=null;
			
			if(type == null)
				return null;
			
			comparator=switch(type) {
			case SIZE->(x,y)->x.getResponseSize()-y.getResponseSize();
			case IP->(x,y)->x.getIp().compareTo(y.getIp());
			case HTTPVERSION->(x,y)->x.getHttpVersion().compareTo(y.getHttpVersion());
			case AGENT->(x,y)->x.getUserAgent().compareTo(y.getUserAgent());
			case DATETIME->(x,y)->x.getDateTime().compareTo(y.getDateTime());
			case SC->(x,y)->Short.compare(x.getResponseCode(),(y.getResponseCode()));
			case REFFERER->(x,y)->x.getReferrer().compareTo(y.getReferrer());
			case URL->(x,y)->x.getRequest().compareTo(y.getRequest());
			default->null;
			};
			
			return comparator;
		}
	
}
