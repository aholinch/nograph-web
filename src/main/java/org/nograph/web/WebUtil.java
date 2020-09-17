/* 

Copyright 2020 aholinch

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/
package org.nograph.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nograph.GraphQuery;
import org.nograph.GraphQuery.Criterion;
import org.nograph.GraphQuery.SetCriterion;
import org.nograph.GraphQuery.SimpleCriterion;

public class WebUtil 
{
	public static final String JSON = "application/json";
	
	/**
	 * Ensure request params are treated as UTF-8 if no encoding specified.
	 * 
	 * @param request
	 */
	public static void setUTF8(HttpServletRequest request)
	{
		try
		{
			request.setCharacterEncoding("UTF-8");
		}
		catch(Exception ex)
		{
			// quiet!
		}
	}
	
	/**
	 * Ensure response encoding is UTF-8.
	 * 
	 * @param request
	 */
	public static void setUTF8(HttpServletResponse response)
	{
		try
		{
			response.setCharacterEncoding("UTF-8");
		}
		catch(Exception ex)
		{
			// quiet!
		}
	}
	
	/**
	 * For a URL like /[part1]/[part2] or /[part1]/[part2]/ it will return [part2].
	 * 
	 * @param request
	 * @return
	 */
    public static String getLastPath(HttpServletRequest request)
    {
    	String url = request.getPathInfo();
    	if(url != null)
    	{
    		if(url.endsWith("/")) url = url.substring(0,url.length()-1);
    		if(url.startsWith("/")) url = url.substring(1);
    		
    		int ind = url.lastIndexOf('/');
    		if(ind > 0)
    		{
    			url = url.substring(ind+1).trim();
    		}
    	}
    	return url;
    }
    
    public static String getParameter(HttpServletRequest request, String paramName)
    {
    	return getParameter(request,paramName,null);
    }
    
    
    public static String getParameter(HttpServletRequest request, String paramName, String defaultVal)
    {
    	String out = null;
    	out = request.getParameter(paramName);
    	if(out == null)
    	{
    		Object obj = request.getAttribute(paramName);
    		if(obj != null)
    		{
    			out = obj.toString();
    		}
    	}
    	
    	if(out == null)
    	{
    		out = defaultVal;
    	}
    	
    	return out;
    }
    
    public static int getIntParameter(HttpServletRequest request, String param, int defaultVal)
    {
    	int num = defaultVal;
    	
    	String str = getParameter(request,param,null);
    	if(str != null)
    	{
    		try
    		{
    			num = Integer.parseInt(str.trim());
    		}
    		catch(Exception ex)
    		{
    			num = defaultVal;
    		}
    	}
    	
    	return num;
    }
    
    public static String listToJSONArray(List<String> strs)
    {
		StringBuffer sb = new StringBuffer(100);
		if(strs == null || strs.size() == 0) return "";
		
		int size = strs.size();
		String str = null;
		sb.append("[");
		str = strs.get(0);
		sb.append("\"").append(str).append("\"");

		for(int i=1; i<size; i++)
		{
			str = strs.get(i);
			sb.append(",");
			sb.append("\"").append(str).append("\"");
		}
		sb.append("]");
		return sb.toString();
    }
    
    public static GraphQuery parseQuery(HttpServletRequest request)
    {
    	return parseQuery(request,null);
    }
    
    public static GraphQuery parseQuery(HttpServletRequest request, List<String> ignoreParams)
    {
    	if(ignoreParams == null) ignoreParams = new ArrayList<String>();
    	
    	GraphQuery query = new GraphQuery();
    	
    	Map<String,String[]> pm = request.getParameterMap();
    	
    	SetCriterion setcrit = null;
    	SimpleCriterion  crit = null;
    	String name = null;
    	String vals[] = null;
    	int size = 0;
    	
    	Map<String,Criterion> cm = new HashMap<String,Criterion>();
    	
    	List<String> names = new ArrayList<String>(pm.keySet());
    	size = names.size();
    	
    	for(int i=0; i<size; i++)
    	{
    		setcrit = null;
    		
    		name = names.get(i);
    		if(ignoreParams.contains(name)) continue;
    		
    		vals = pm.get(name);
    		if(vals.length>1)
    		{
    			setcrit = new SetCriterion();
    			setcrit.setSetOperation(SetCriterion.COMB_OR);
        		cm.put(name, setcrit);
    		}
    		
    		for(int j=0; j<vals.length; j++)
    		{
    			crit = new SimpleCriterion(name,vals[j],SimpleCriterion.OP_EQUAL);
    			if(setcrit !=null)setcrit.addCriterion(crit);
    		}

    		if(setcrit == null)
    		{
    			cm.put(name, crit);
    		}
    	}
    	
    	names = new ArrayList<String>(cm.keySet());
    	size = names.size();
    	if(size == 1)
    	{
    		name = names.get(0);
    		query.setCriterion(cm.get(name));
    	}
    	else if(size > 1)
    	{
    		setcrit = new SetCriterion();
    		setcrit.setSetOperation(SetCriterion.COMB_AND);
    		
	    	for(int i=0; i<size; i++)
	    	{
	    		name = names.get(i);
	    		setcrit.addCriterion(cm.get(name));
	    	}
	    	query.setCriterion(setcrit);
    	}
    	return query;
    }
    
    public static String readRequestBody(HttpServletRequest request)
    {
    	String out = null;
    	
    	return out;
    }
}
