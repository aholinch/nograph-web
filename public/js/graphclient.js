/*! nograph-web 
© 2020 aholinch 
https://github.com/aholinch/nograph-web/blob/master/LICENSE
*/

// class syntax not supported by IE
class GraphClient{
	// we can put host and context info here if needed
	// otherwise a local install is easy to call
	
	constructor(){
		
	}

	getNodeCounts(callback)
	{
		$.getJSON( "graph/nodecounts", callback); 
	}

	listNodes(nodetype,callback)
	{
		$.getJSON( "graph/nodes?type="+nodetype+"&max=20", callback); 
	}
	
	searchNodes(nodetype,max,params,callback)
	{
		var url = "graph/nodes?type="+nodetype+"&max="+max;
		if(!(params == null || params === undefined))
		{
			var keys = Object.keys(params);
			var p = null;
			var v = null;
			var len = keys.length;
			for(var i=0; i<len; i++)
			{
				p = keys[i];
				v = params[p];
				url = url + "&"+p+"="+v;
			}
		}
		$.getJSON(url, callback); 
	}
	
	getPropertyTypes(nodetype,callback)
	{
		$.getJSON( "graph/propsfornode?type="+nodetype, callback); 
	}
	
	getNodeByID(nodeID,callback)
	{
		$.getJSON( "graph/node/"+nodeID, callback); 		
	}
	
	getRelsForNode(nodeID,callback)
	{
		$.getJSON( "graph/relsfornode/"+nodeID, callback); 				
	}
}

// static utility functions

function groupNodesByType(nodes)
{
}

function groupRelsByType(rels)
{
	if(rels == null || rels === undefined) return null;
	
	var len = rels.length;
	var rm = new Map();
	var rel = null;
	var type = null;
	var rellist = null;
	for(var i=0; i<len; i++)
	{
		rel = rels[i];
		type = rel.type;
		rellist = rm.get(type);
		if(rellist == null || rellist === undefined)
		{
			rellist = [];
			rm.set(type,rellist);
		}
		rellist.push(rel);
	}
	return rm;
}