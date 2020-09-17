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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nograph.GraphManager;
import org.nograph.GraphQuery;
import org.nograph.GraphQuery.SimpleCriterion;
import org.nograph.NoGraph;
import org.nograph.NoGraphConfig;
import org.nograph.Node;
import org.nograph.Relationship;
import org.nograph.util.json.JSONArray;
import org.nograph.util.json.JSONObject;

public class GraphServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(GraphServlet.class.getName());

	public static final String NODETYPES    = "nodetypes";
	public static final String NODECOUNTS   = "nodecounts";
	public static final String RELTYPES     = "reltypes";
	public static final String RELCOUNTS    = "relcounts";
	public static final String PROPS4NODE   = "propsfornode";
	public static final String PROPS4REL    = "propsforrel";
	public static final String NODES        = "nodes";
	public static final String RELS4NODE    = "relsfornode";
	public static final String RELS         = "rels";
	public static final String NODE         = "node";
	public static final String REL          = "rel";
	
	protected boolean allowDelete = true;
	protected boolean allowInsertUpdateData = true;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String path = request.getPathInfo();
		
		// some strings are subsets of others so be careful of the order
		if(path.contains(NODETYPES))
		{
			handleGetNodeTypes(request,response);
		}
		else if(path.contains(NODECOUNTS))
		{
			handleNodeCounts(request,response);
		}
		else if(path.contains(RELTYPES))
		{
			handleGetRelTypes(request,response);
		}
		else if(path.contains(RELCOUNTS))
		{
			handleRelCounts(request,response);
		}
		else if(path.contains(PROPS4NODE))
		{
			handlePropsForNode(request,response);
		}
		else if(path.contains(PROPS4REL))
		{
			handlePropsForRel(request,response);
		}
		else if(path.contains(NODES))
		{
			handleNodes(request,response);
		}
		else if(path.contains(RELS4NODE))
		{
			handleRelsForNode(request,response);
		}
		else if(path.contains(RELS))
		{
			handleRels(request,response);
		}
		else if(path.contains(NODE))
		{
			handleNodeByID(request,response);
		}
		else if(path.contains(REL))
		{
			handleRelByID(request,response);
		}
	}
	
	protected GraphManager getGraphManager(HttpServletRequest request, String requestType)
	{
		// use just one for now
    	NoGraphConfig config = NoGraph.getInstance().getConfig();

    	config.setProperty("graphman.class", "org.nograph.impl.ElasticGraphManager");
    	config.setProperty("meta.dir","/data/dev/workspace/vger/");
    	
    	GraphManager gm = NoGraph.getInstance().getGraphManager();
    	
    	return gm;
	}

	public void handleGetNodeTypes(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			GraphManager gm = getGraphManager(request,NODETYPES);
			List<String> types = gm.getNodeTypes();
			String str = WebUtil.listToJSONArray(types);
			
			response.setContentType(WebUtil.JSON);
			PrintWriter pw = response.getWriter();
			pw.write(str);
			pw.flush();
			pw.close();
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Error getting node types",ex);
		}
	}
	
	public void handleNodeCounts(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			GraphManager gm = getGraphManager(request,NODETYPES);
			Map<String,Long> m = gm.getNodeCountsByType();
			
			JSONObject obj = new JSONObject(m);
			String str = obj.toString();
			
			response.setContentType(WebUtil.JSON);
			PrintWriter pw = response.getWriter();
			pw.write(str);
			pw.flush();
			pw.close();
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Error getting node counts",ex);
		}
	}

	public void handleGetRelTypes(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			GraphManager gm = getGraphManager(request,RELTYPES);
			List<String> types = gm.getRelationshipTypes();
			String str = WebUtil.listToJSONArray(types);
			
			response.setContentType(WebUtil.JSON);
			PrintWriter pw = response.getWriter();
			pw.write(str);
			pw.flush();
			pw.close();
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Error getting rel types",ex);
		}
	}
	
	public void handleRelCounts(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			GraphManager gm = getGraphManager(request,RELTYPES);
			Map<String,Long> m = gm.getRelationshipCountsByType();
			
			JSONObject obj = new JSONObject(m);
			String str = obj.toString();
			
			response.setContentType(WebUtil.JSON);
			PrintWriter pw = response.getWriter();
			pw.write(str);
			pw.flush();
			pw.close();
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Error getting rel counts",ex);
		}
	}

	public void handlePropsForNode(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			GraphManager gm = getGraphManager(request,PROPS4NODE);
			String type = WebUtil.getParameter(request, "type");
			if(type == null)
			{
				type = WebUtil.getLastPath(request);
			}
			
			logger.info("node type = " + type);
			List<String> props = gm.getPropertyNamesForNodeType(type);
			String str = WebUtil.listToJSONArray(props);
			if(str==null || str.trim().length() == 0) str = "[]";
			response.setContentType(WebUtil.JSON);
			PrintWriter pw = response.getWriter();
			pw.write(str);
			pw.flush();
			pw.close();
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Error getting props for node",ex);
		}
	}
	
	public void handlePropsForRel(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			GraphManager gm = getGraphManager(request,PROPS4REL);
			String type = WebUtil.getParameter(request, "type");
			if(type == null)
			{
				type = WebUtil.getLastPath(request);
			}
			
			logger.info("rel type = " + type);
			List<String> props = gm.getPropertyNamesForRelationshipType(type);
			String str = WebUtil.listToJSONArray(props);
			if(str==null || str.trim().length() == 0) str = "[]";

			response.setContentType(WebUtil.JSON);
			PrintWriter pw = response.getWriter();
			pw.write(str);
			pw.flush();
			pw.close();
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Error getting props for node",ex);
		}
	}
	
	public void handleNodeByID(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			GraphManager gm = getGraphManager(request,NODE);
			String id = WebUtil.getParameter(request, "id");
			if(id == null)
			{
				id = WebUtil.getLastPath(request);
			}
			
			logger.info("node id = " + id);
			Node node = gm.getNode(id);
			
			if(node != null)
			{
				response.setContentType(WebUtil.JSON);
				PrintWriter pw = response.getWriter();
				String str = node.toJSONString();
				pw.write(str);
				pw.flush();
				pw.close();
			}
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Error getting node",ex);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void handleNodes(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			GraphManager gm = getGraphManager(request,NODE);
			
			List<Node> nodes = null;
			
			List<String> ignore = new ArrayList<String>();
			ignore.add("max");
			
			int max = WebUtil.getIntParameter(request, "max", 1000);
			
			GraphQuery gq = WebUtil.parseQuery(request,ignore);
			gq.setMaxResults(max);
			
			if(gq.getCriterion() == null)
			{
				// type may be the last item on the path
				if(max < 1)
				{
					gq.setMaxResults(20);
				}
				String type = WebUtil.getLastPath(request);
				SimpleCriterion crit = new SimpleCriterion("type",type,SimpleCriterion.OP_EQUAL);
				gq.setCriterion(crit);
			}
			
			nodes = gm.findNodes(gq);
			
			response.setContentType(WebUtil.JSON);
			PrintWriter pw = response.getWriter();
			
			JSONArray arr = new JSONArray((java.util.Collection)nodes);
			
			String str = arr.toString();
			pw.write(str);
			pw.flush();
			pw.close();
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Error getting node",ex);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void handleRels(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			GraphManager gm = getGraphManager(request,NODE);
			
			List<Relationship> rels = null;
			
			List<String> ignore = new ArrayList<String>();
			ignore.add("max");
			
			int max = WebUtil.getIntParameter(request, "max", 1000);
			
			GraphQuery gq = WebUtil.parseQuery(request,ignore);
			gq.setMaxResults(max);
			
			if(gq.getCriterion() == null)
			{
				// type may be the last item on the path
				if(max < 1)
				{
					gq.setMaxResults(20);
				}
				
				String type = WebUtil.getLastPath(request);
				SimpleCriterion crit = new SimpleCriterion("type",type,SimpleCriterion.OP_EQUAL);
				gq.setCriterion(crit);
			}
			
			rels = gm.findRelationships(gq);
			
			response.setContentType(WebUtil.JSON);
			PrintWriter pw = response.getWriter();
			
			JSONArray arr = new JSONArray((java.util.Collection)rels);
			
			String str = arr.toString();
			pw.write(str);
			pw.flush();
			pw.close();
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Error getting node",ex);
		}
	}
	
	public void handleRelsForNode(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			GraphManager gm = getGraphManager(request,RELS4NODE);
			String id = WebUtil.getParameter(request, "id");
			if(id == null)
			{
				id = WebUtil.getLastPath(request);
			}
			
			logger.info("node id = " + id);
			Node node = gm.getNode(id);
			
			if(node != null)
			{
				List<Relationship> rels = gm.findRelatedNodes(id);
				if(rels != null)
				{
					Map<String,Node> nodeMap = new HashMap<String,Node>();
					
					Map<String,Object> out = new HashMap<String,Object>();
					out.put("nodeid", id);
					
					int size = rels.size();
					
					List<Map<String,Object>> relStrs = null;
					Map<String,Map<String,Object>> rm = new HashMap<String,Map<String,Object>>();
					Relationship rel = null;
					Node n = null;
					for(int i=0; i<size; i++)
					{
						rel = rels.get(i);
						n = rel.getNode1();
						id = n.getID();
						nodeMap.put(id, n);
						n = rel.getNode2();
						id = n.getID();
						nodeMap.put(id, n);
						id = rel.getID();
						if(rm.get(id)==null)
						{
							rm.put(id, rel.getMinPropertyMap());
						}
					}
					
					relStrs = new ArrayList<Map<String,Object>>(rm.values());
					out.put("nodes", nodeMap);
					out.put("rels",relStrs);
					
					JSONObject obj = new JSONObject(out);
					String str = obj.toString();

					response.setContentType(WebUtil.JSON);
					PrintWriter pw = response.getWriter();
					pw.write(str);
					pw.flush();
					pw.close();
				}
			}
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Error getting node",ex);
		}
	}
	
	public void handleRelByID(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			GraphManager gm = getGraphManager(request,REL);
			String id = WebUtil.getParameter(request, "id");
			if(id == null)
			{
				id = WebUtil.getLastPath(request);
			}
			
			logger.info("rel id = " + id);
			Relationship rel = gm.getRelationship(id,true);
			
			if(rel != null)
			{
				response.setContentType(WebUtil.JSON);
				PrintWriter pw = response.getWriter();
				String str = rel.toJSONString();
				pw.write(str);
				pw.flush();
				pw.close();
			}
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Error getting rel",ex);
		}
	}

	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try
		{
			if(!allowDelete)
			{
				response.sendError(403,"Deletes are not allowed");
				return;
			}
			
			String path = request.getPathInfo();
			
			String id = WebUtil.getParameter(request, "id");
			if(id == null)
			{
				id = WebUtil.getLastPath(request);
			}
			
			boolean doNodes = true;
			
			if(path.contains(NODE))
			{
				doNodes = true;
			}
			else if(path.contains(REL))
			{
				doNodes = false;
			}
			
			List<String> ids = null;
			
			// check for body
			String body = WebUtil.readRequestBody(request);
			if(body != null && body.trim().length()>0)
			{
				String sa[] = body.split(",");
				int size = sa.length;
				ids = new ArrayList<String>();
				for(int i=0; i<size; i++)
				{
					ids.add(sa[i].trim());
				}
			}
			
			GraphManager gm = getGraphManager(request,path);
			
			if(doNodes)
			{
				if(ids == null)
				{
					gm.deleteNode(id);
				}
				else
				{
					gm.deleteNodesByID(ids);
				}
			}
			else
			{
				if(ids == null)
				{
					gm.deleteRelationship(id);
				}
				else
				{
					gm.deleteRelationshipsByID(ids);
				}			
			}
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Error doing delete",ex);
		}

	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try
		{
			if(!allowInsertUpdateData)
			{
				response.sendError(403,"Data modifications are not allowed");
				return;
			}
	
			String path = request.getPathInfo();
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Error doing update",ex);
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try
		{
			if(!allowInsertUpdateData)
			{
				response.sendError(403,"Data modifications are not allowed");
				return;
			}
	
			String path = request.getPathInfo();
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Error doing create",ex);
		}
	}
}
