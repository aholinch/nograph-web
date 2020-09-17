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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nograph.asset.Asset;
import org.nograph.asset.AssetManager;
import org.nograph.util.FileUtil;
import org.nograph.util.json.JSONArray;

/**
 * Facilitates streaming asset content and searching metadata.
 * 
 * @author aholinch
 *
 */
public class AssetServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(AssetServlet.class.getName());
	
	protected AssetManager assetManager = null;
	
	public static final int BUFFERSIZE = 64*1024;
	
    public void init(ServletConfig config) throws ServletException 
    {
        super.init(config);
		
        assetManager = new AssetManager();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String url = WebUtil.getLastPath(request);
		String uuid = url;
		if(url == null || request.getServletPath().contains(url))
		{
			uuid = WebUtil.getParameter(request, "uuid");
		}
		
		if(request.getPathInfo().contains("search"))
		{
			searchAssets(request,response,uuid);
		}
		else
		{
			streamAsset(request,response,uuid);
		}
	}
	
	/**
	 * Search for assets.
	 * 
	 * @param request
	 * @param response
	 * @param uuid
	 * @throws ServletException
	 * @throws IOException
	 */
	public void searchAssets(HttpServletRequest request, HttpServletResponse response, String uuid) throws ServletException, IOException 
	{
		Map<String,String[]> map = request.getParameterMap();
		
		List<String> props = new ArrayList<String>(map.keySet());
		
		StringBuffer sb = new StringBuffer(1000);
		
		if(props.size() == 0)
		{
			logger.info("No search parameters passed");
			response.setContentType("text/plain");
			PrintWriter pw = response.getWriter();
			pw.write("No search parameters passed");
			pw.flush();
			pw.close();
			return;
		}
		
		String prop = null;
		String sa[] = null;
		
		prop = props.get(0);
		sa = map.get(prop);
		if(sa != null && sa.length>0)
		{
			sb.append("(");
			sb.append(prop).append(":").append(sa[0]);
			for(int j=1; j<sa.length; j++)
			{		
				sb.append(" OR ");
				sb.append(prop).append(":").append(sa[j]);
			}
			sb.append(")");
		}
		
		for(int i=1; i<props.size(); i++)
		{
			prop = props.get(i);
			sa = map.get(prop);
			if(sa != null && sa.length>0)
			{
				sb.append(" AND (");
				sb.append(prop).append(":").append(sa[0]);
				for(int j=1; j<sa.length; j++)
				{		
					sb.append(" OR ");
					sb.append(prop).append(":").append(sa[j]);
				}
				sb.append(")");
			}
		}
		
		String query = sb.toString();
		logger.info("Asset search query = " + query);
		try
		{
			response.setContentType(WebUtil.JSON);
			List<Asset> assets = assetManager.findAssets(query);
			PrintWriter pw = response.getWriter();
			@SuppressWarnings("rawtypes")
			JSONArray arr = new JSONArray((Collection)assets);
			pw.write(arr.toString());
			pw.flush();
			pw.close();
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE,"Error searching assets",ex);
		}
	}
	
	/**
	 * Stream the asset bytes back in the response.
	 * 
	 * @param request
	 * @param response
	 * @param uuid
	 * @throws ServletException
	 * @throws IOException
	 */
	public void streamAsset(HttpServletRequest request, HttpServletResponse response, String uuid) throws ServletException, IOException 
	{
		logger.info("UUID = " + String.valueOf(uuid));
		
		Asset asset = assetManager.getAsset(uuid);
		if(asset == null)
		{
			response.setContentType("text/plain");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			PrintWriter pw = response.getWriter();
			pw.write("No asset for uuid ");
			pw.write(uuid);
			pw.flush();
			pw.close();
			return;
		}
		else
		{
			String mime = FileUtil.getMimeType(asset.getFilename());
			if(mime == null)
			{
				mime = FileUtil.GENERIC_BINARY;
			}
			response.setContentType(mime);
			Long size = asset.getFilesize();
			if(size != null && size > 0)
			{
				response.setContentLength(size.intValue());
			}
			response.setHeader("Content-Disposition", "inline; filename=\""+asset.getFilename()+"\""); 
			OutputStream os = null;
			InputStream is = null;
			
			try
			{
				os = response.getOutputStream();
				is = assetManager.getAssetStream(uuid);
				
				byte ba[] = new byte[BUFFERSIZE];
				int numRead = is.read(ba);
				while(numRead > 0)
				{
					os.write(ba,0,numRead);
					numRead = is.read(ba);
				}
				
				os.flush();
			}
			catch(Exception ex)
			{
				logger.log(Level.WARNING,"Error streaming asset",ex);
			}
			finally
			{
				FileUtil.close(os);
				FileUtil.close(is);
			}
		}
		
		return;
    }	
}
