<?xml version="1.0" encoding="UTF-8"?>
<!--Steve Kalmar 2816596 14/03/2012-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:strip-space elements="TIME"/>
	<xsl:output method="xml" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" indent="yes"/>
	<xsl:template match="/">
		<html lang="en" dir="ltr" xmlns="http://www.w3.org/1999/xhtml">
			<xsl:apply-templates select="MILES"/>
		</html>
	</xsl:template>
	<xsl:template match="MILES">
		<head xmlns="http://www.w3.org/1999/xhtml">
			<title>MILES ECLIPSE PLUGIN PROGRAMMING SESSION REPORT</title>
			<script type="text/javascript">
				function toggleDiv(bodyID, headerID, callObjID){
					if(document.getElementById(bodyID).style.display == 'none'){
						document.getElementById(bodyID).style.display = 'block';
						document.getElementById(headerID).style.margin = '8 2.5% 0';
						document.getElementById(callObjID).innerHTML = "Hide";
					}else{
						document.getElementById(bodyID).style.display = 'none';
						document.getElementById(headerID).style.margin = '8 2.5% 8';
						document.getElementById(callObjID).innerHTML = "Show";
					}
				}
			</script>
			<style type="text/css">
				body, div, dl, dt, dd, ul, ol, li, h1, h2, h3, h4, h5, h6, pre, code, form, fieldset, legend, input, button, textarea, p, blockquote, th, td {
					margin: 0;
					padding: 0;
				}
				body {
					color: #3E3E3E;
					font-size: 13px;
					line-height: 1.23;
					margin: 0 2.5%;
					min-width: 650px;
					width: auto;
				}
				.mainHeader {
					box-shadow: -2px 2px 2px #c8c8c8;
					position: relative;
					background-color: #7192a8;
					border: 1px solid #5a7f97;
					border-top-left-radius: 5px;
					border-top-right-radius: 5px;
					clear: both;
					color: #FFFFFF;
					float: left;
					font: bold 12px Arial,Tahoma,Calibri,Verdana,Geneva,sans-serif;
					margin-top: 8px;
					width: 99%;
				}
				.mainBody {
					border: 1px solid #c4c4c4;
					border-top: 1px solid #FFFFFF;
					background-color:#FFFFFF;
					display: block;
					float: left;
					position: relative;
					width: 99%;
					box-shadow: -2px 2px 2px #c8c8c8;
					font: 11px Tahoma,Calibri,Verdana,Geneva,sans-serif;
					color: #417394;
				}
				.session_info_elements {
					width: 33.33%;
					float: left;
				}
				.show_hide {
					text-decoration:none;
					float:right;
					font-family:Courier New, monospace;
					margin-right:10px;
				}
				.subHeader {
					margin: 8px 0.5% 0px;
				}
				.subBody {
					margin: 0px 0.5% 10px;
				}
				.file {
					background-color: #CEDFEB;
					width: 100%;
				}
				#compile_info, #session_data {
					padding-top: 8px;
					padding-bottom: 8px;
				}
			</style>
		</head>
		<body xmlns="http://www.w3.org/1999/xhtml" style="width:960px;">
				<div class="mainHeader"><h2><span style="margin-left:10px;">MILES Session Info</span><a id="sessionShow" href="javascript:;" onclick="toggleDiv('session_info', 'sessionShow')" class="show_hide">Hide</a></h2></div>
				<xsl:apply-templates select="SESSION_INFO"/>
				<div class="mainHeader"><h2><span style="margin-left:10px;">MILES Session Info</span><a id="dataShow" href="javascript:;" onclick="toggleDiv('session_data', 'dataShow')" class="show_hide">Hide</a></h2></div>
				<div id="session_data" class="mainBody">
						<div class="mainHeader subHeader"><h2><span style="margin-left:10px;">Compile Instances</span><a id="compileShow" href="javascript:;" onclick="toggleDiv('compile_info', 'compileShow')" class="show_hide">Hide</a></h2></div>
						<xsl:apply-templates select="SESSION_DATA"/>
				</div>
		</body>
	</xsl:template>
	<xsl:template match="SESSION_INFO">
		<div xmlns="http://www.w3.org/1999/xhtml" id="session_info" class="mainBody">
			<div class="session_info_elements"><h2 style="margin-left:10px;">Session Start Time: <xsl:value-of select="SESSION_START_TIME"/></h2></div>
			<div class="session_info_elements"><h2>Student ID: <xsl:value-of select="STUDENT_ID"/></h2></div>
			<div class="session_info_elements" style="float:right;"><h2>Assignment: <xsl:value-of select="ASSIGNMENT"/></h2></div>
		</div>
	</xsl:template>
	<xsl:template match="SESSION_DATA">
		<div xmlns="http://www.w3.org/1999/xhtml" id="compile_info" class="mainBody subBody">
			<xsl:apply-templates select="COMPILE_INSTANCE"/>
		</div>
	</xsl:template>
	<xsl:template match="COMPILE_INSTANCE">
		<div xmlns="http://www.w3.org/1999/xhtml" class="mainHeader subHeader">
			<xsl:attribute name="id">h<xsl:value-of select="TIME/@UTC"/></xsl:attribute>
			<h2>
				<span style="margin-left:10px;">Compile Instance: <xsl:apply-templates select="TIME"/></span>
				<a href="javascript:;" class="show_hide">
					<xsl:attribute name="id">a<xsl:value-of select="TIME/@UTC"/></xsl:attribute>
					<xsl:attribute name="onclick">toggleDiv('b<xsl:value-of select="TIME/@UTC"/>', 'h<xsl:value-of select="TIME/@UTC"/>', 'a<xsl:value-of select="TIME/@UTC"/>')</xsl:attribute>
					Hide
				</a>
			</h2>
		</div>
		<div xmlns="http://www.w3.org/1999/xhtml" class="mainBody subBody">
			<xsl:attribute name="id">b<xsl:value-of select="TIME/@UTC"/></xsl:attribute>
			<xsl:apply-templates select="PACKAGES"/>
			<xsl:if test="FILES">
				<div class="mainHeader subHeader">
					<h2>
						<span style="margin-left:10px;">UML File(s)</span>
					</h2>
				</div>
				<div class="mainBody subBody">
					<xsl:apply-templates select="FILES/FILE"/>
				</div>
			</xsl:if>
		</div>
	</xsl:template>
	<xsl:template match="TIME">
		<xsl:value-of select="normalize-space(text()[1])"/>
	</xsl:template>
	<xsl:template match="PACKAGES">
		<xsl:apply-templates select="PACKAGE"/>
	</xsl:template>
	<xsl:template match="PACKAGE">
		<div xmlns="http://www.w3.org/1999/xhtml" class="mainHeader subHeader">
			<xsl:attribute name="id">h<xsl:value-of select="NAME"/></xsl:attribute>
			<h2>
				<span style="margin-left:10px;">Package: <xsl:value-of select="NAME"/></span>
				<a href="javascript:;" class="show_hide">
					<xsl:attribute name="id">a<xsl:value-of select="NAME"/></xsl:attribute>
					<xsl:attribute name="onclick">toggleDiv('b<xsl:value-of select="NAME"/>', 'h<xsl:value-of select="NAME"/>', 'a<xsl:value-of select="NAME"/>')</xsl:attribute>
					Hide
				</a>
			</h2>
		</div>
		<div xmlns="http://www.w3.org/1999/xhtml" class="mainBody subBody">
			<xsl:attribute name="id">b<xsl:value-of select="NAME"/></xsl:attribute>
			<xsl:apply-templates select="FILES/FILE"/>
		</div>
	</xsl:template>
	<xsl:template match="FILES/FILE">
			<div xmlns="http://www.w3.org/1999/xhtml" class="file"><xsl:value-of select="NAME"/></div>
			<div xmlns="http://www.w3.org/1999/xhtml" style="width:100%;">
				<div style="width:100%;">
					<div style="margin-left: 10px; font-weight:bold;">Compile Problems</div>
					<xsl:apply-templates select="COMPILE_PROBLEMS"/>
				</div>
				<div style="width:100%;">
					<div style="margin-left: 10px; font-weight:bold;">Source</div>
					<pre style="margin-left: 10px; overflow:auto"><xsl:value-of select="SOURCE"/></pre>
				</div>
			</div>
	</xsl:template>
	<xsl:template match="COMPILE_PROBLEMS">
		<div xmlns="http://www.w3.org/1999/xhtml" style="margin-left: 10px;">
			<xsl:choose>
				<xsl:when test="ERROR"><xsl:apply-templates/></xsl:when>
				<xsl:when test="WARNING"><xsl:apply-templates/></xsl:when>
				<xsl:when test="INFORMATION"><xsl:apply-templates/></xsl:when>
				<xsl:otherwise>
					<div>No Problems Found</div>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>
	<xsl:template match="ERROR">
		<div xmlns="http://www.w3.org/1999/xhtml" style="color:red;">Error @ Line <xsl:value-of select="LINE"/>: <xsl:value-of select="MESSAGE"/></div>
	</xsl:template>
	<xsl:template match="WARNING">
		<div xmlns="http://www.w3.org/1999/xhtml" style="color:orange;">Warning @ Line <xsl:value-of select="LINE"/>: <xsl:value-of select="MESSAGE"/></div>
	</xsl:template>
	<xsl:template match="INFORMATION">
		<div xmlns="http://www.w3.org/1999/xhtml">Information @ Line <xsl:value-of select="LINE"/>: <xsl:value-of select="MESSAGE"/></div>
	</xsl:template>
</xsl:stylesheet>