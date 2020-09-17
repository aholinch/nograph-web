/*! nograph-web 
© 2020 aholinch 
https://github.com/aholinch/nograph-web/blob/master/LICENSE
*/


function getParamFromLoc(loc,param)
{
	var str = new String(loc);
	var ind = str.indexOf('?');
	if(ind < 0) return null;
	
	str = str.substring(ind+1).trim();
	ind = str.indexOf(param+'=');
	if(ind < 0) return null;
	ind += 1 +param.length;
	str = str.substring(ind);
	ind = str.indexOf('&');
	if(ind > -1)str = str.substring(0,ind);
	return str;
}

function isEmpty(val)
{
	if(val == null || val === undefined) return true;
	var s = new String(val);
	if(s.trim().length == 0) return true;
	return false;
}

function getFirstNonEmptyProperty(node,props)
{
	if(node == null || node === undefined) return "";
	if(props == null || props === undefined || props.length == 0) return node.id;
	
	var out = null;
	var len = props.length;
	var pname = null;
	var empty = true;
	for(var i=0; i<len; i++)
	{
		pname = props[i];
		out = node[pname];
		if(isEmpty(out))
		{
			out = node[pname.toLowerCase()];
		}
		
		if(!isEmpty(out))
		{
			empty = false;
			break;
		}
	}
	
	if(empty) out = "";
	
	return out;
}

// a basic render function for node properties
// uses su_getTitleProps and su_getNodePropertyAndDisplayNames
function nr_basicRenderNode(node,divid,titleid)
{
	var div = $('#'+divid);
	div.empty();
	var title = $('#'+titleid);
	var ttext = su_getNodeShortLabel(node);
	title.text(ttext);
	document.title=ttext;

	// loop over properties
	parr = su_getNodePropertyAndDisplayNames(node.type,node);
	var len = parr.length;
	var prop = null;
	var disp = null;
	var val = null;
	var row = null;
	var lc = null;
	var rc = null;
	for(var i=0; i<len; i++)
	{
		prop = parr[i][0];
		disp = parr[i][1];
		val = node[prop];
		if(isEmpty(val)) val = node[prop.toLowerCase()];
		if(isEmpty(val)) continue;
		
		row = $('<div class="w3-row"></div>');
		lc = $('<div class="w3-col s1 w3-theme-d3 w3-center"><p>'+disp+'</p></div>');
		rc = $('<div class="w3-col s5 w3-theme-l3"><p>&nbsp;&nbsp;'+val+'</p></div>');
		row.append(lc);
		row.append(rc);
		div.append(row);
	}
} // end nr_basicRenderNode

function nr_basicRenderRels(relInfo,divid)
{
	var div = $('#'+divid);
	div.empty();
	console.log("Rendering rel");
	
	var nodeID = relInfo.nodeid;
	var rels = relInfo.rels;
	var relMap = groupRelsByType(rels);
	console.log(relMap);
	var rts = [];
	var itr = relMap.keys();
	for(var i=0; i<relMap.size; i++)
	{
		rts.push(itr.next().value);
	}
	console.log(rts);
	var container = $('<div class="w3-container"></div>');
	
	var tabbuttons = $('<div class="w3-bar w3-theme-d3"></div>');
	
	var button = null;
	
	var tabarr = [];
	var tab = null;
	var len = rts.length;
	var rt = null;
	var title = null;
	for(var i=0; i<len; i++)
	{
		rt = rts[i];
		title = su_getRelTypeTitle(rt);
		button = $('<button class="w3-bar-item w3-button tablink" onclick="openRelTab(event,\''+rt+'\')">'+title+'</button>');
		tabbuttons.append(button);
		tab = $('<div id="rel_'+rt+'" class="w3-container w3-border relinfo"></div>');
		tabarr.push(tab);
	}
	
	container.append(tabbuttons);
	for(var i=0; i<len; i++)
	{
		container.append(tabarr[i]);
	}
	div.append(container);
	
	for(var i=0; i<len; i++)
	{
		rt = rts[i];
		div = $('#rel_'+rt);
		rels = relMap.get(rt);
		var row = null;
		var lc = null;
		var rc = null;
		var rel = null;
		for(var j=0; j<rels.length; j++)
		{
			//console.log(rt+"\t"+rels[j]);
			rel = rels[j];
			row = nr_getRelRow(nodeID,rt,rel);
			div.append(row);
		}
	}
	
	if(!(closeTabs === undefined))
	{
		closeTabs();
	}
	
} // nr_basicRenderRels

function nr_getRelRow(nodeid, reltype, rel, nodes)
{
	var row = null;
	var lc = null;
	var rc = null;
	var hid = null;
	
	var linkednode = rel.node1;
	var nid = 1;
	if(linkednode.id==nodeid)
	{
		linkednode = rel.node2;
		nid = 2;
	}

	row = $('<div class="w3-row"></div>');
	lc = $('<div class="w3-col s1 w3-theme-d3 w3-center"><p>'+rel.type+'</p></div>');
	rc = $('<div class="w3-col s5 w3-theme-l3"><p>&nbsp;&nbsp;'+linkednode.type +' - ' + linkednode.id+'</p></div>');
	hid = $('<input type="hidden" name="'+linkednode.type+'" value="'+linkednode.id+'" />');
	rc.append(hid);
	
	row.append(lc);
	row.append(rc);
	
	rc.attr("nodeid",linkednode.id);
	
	row.on('dblclick', 'div', function () {
		var inp = $(this).find("input");
		if(inp == null || inp === undefined) return;
		var nt = inp.attr("name");
		var id = inp.attr("value");
		su_openPageForNode(id, nt);
    } );

	return row;
}
