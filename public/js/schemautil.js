/*! nograph-web 
© 2020 aholinch 
https://github.com/aholinch/nograph-web/blob/master/LICENSE
*/

// These will reside in the global namespace to benefit from all variables
// You can replace these with schema specific code rather quickly

// Gets the column names in order for a result table
function su_getTableColumnNames(nodeType,data)
{
	//console.log("Chance for schema specific override");
	var arr = [];
	
	var names = propMap.get(nodeType);
	if(names == null || names === undefined) names = propMap.get(nodeType.toLowerCase());
	
	var len = names.length;
	if(len > 10) len = 10;
	for(var i=0; i<len; i++)
	{
		var name = names[i];
		name = name.substring(0,1).toUpperCase()+name.substring(1);
		arr.push(name);
	}
	return arr;
}

// Gets the property as it should be in the table cell
function su_getTablePropertyNames(nodeType,data)
{
	//console.log("Chance for schema specific override");
	var arr = [];
	var names = propMap.get(nodeType);
	if(names == null || names === undefined) names = propMap.get(nodeType.toLowerCase());
	
	var len = names.length;
	if(len > 10) len = 10;
	for(var i=0; i<len; i++)
	{
		var name = names[i];
		arr.push(name);
	}
	return arr;	
}

// Allows a schema-specific override of property formatting
function su_getTablePropertyValue(props,propname)
{
	var out = props[propname];
	if(out == null || out === undefined)
	{
		out = props[propname.toLowerCase()];
	}
	if(out == null || out === undefined) out="";

	if(propname.toLowerCase().endsWith("date"))
	{
		out = new Date(+out);
		out = out.getFullYear()+"-"+(out.getMonth()+1)+"-"+out.getDate();
	}
	
	return out;
}

// return a 2d array of type-specific property names and their display names
function su_getNodePropertyAndDisplayNames(nodeType,data)
{
	// we will just reuse the table properties by default
	var props = su_getTablePropertyNames(nodeType,data);
	var disp = su_getTableColumnNames(nodeType,data);
	
	var len = props.length;
	
	var out = [];
	var row = null;
	
	for(var i=0; i<len; i++)
	{
		row = [props[i],disp[i]];
		out.push(row);
	}
	return out;
}

// Get the list of properties to try for the node title text
function su_getTitleProps(nodeType)
{
	return ['id'];
}

// Open the page for the specific node of the given type
function su_openPageForNode(nodeID, nodeType)
{
	//console.log("Opening page for " + nodeID + " of type " + nodeType);
	
	// by default everyone gets nodeinfo.html
	window.open('nodeinfo.html?id='+nodeID);
}

// Feel free to add logic for things like DisplayName, etc.
function su_getNodeShortLabel(node)
{
	var props = su_getTitleProps(node.type);
	var ttext = getFirstNonEmptyProperty(node,props);
	ttext = node.type+ ' - ' + ttext;
	
	return ttext;
}

function su_getRelTypeTitle(reltype)
{
	return reltype.substring(0,1).toUpperCase()+reltype.substring(1);
}

// Call a type-specific node render function if you want
// default depends on noderender.js
function su_callNodeRenderFunction(node,nodeType,nodedivid,titletextid)
{
	nr_basicRenderNode(node,nodedivid,titletextid);
}

//Call a type-specific rel render function if you want
//default depends on noderender.js
function su_callRelRenderFunction(relInfo,reldivid)
{
	nr_basicRenderRels(relInfo,reldivid);
}