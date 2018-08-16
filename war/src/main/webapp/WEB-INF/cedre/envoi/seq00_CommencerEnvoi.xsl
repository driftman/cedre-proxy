<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
							  xmlns:s11="http://schemas.xmlsoap.org/soap/envelope/">
	<xsl:template match="/">
		<s11:Envelope >
		  <s11:Body>
		    <WsEnvoi:seq00_CommencerEnvoi xmlns:WsEnvoi="http://10.89.156.154:9081/CedreWebService/services/WSSynchronisationPDASIEnvoi/">
				<xsl:choose>
					<xsl:when test = "/*/@method = 'GET'">
						<identifiantPDA><xsl:value-of select="//path/component[2]"/></identifiantPDA>
					</xsl:when>
					<xsl:when test = "/*/@method = 'POST'">
						<identifiantPDA><xsl:value-of select="*/body/root/idPDA"/></identifiantPDA>
					</xsl:when>
				</xsl:choose>
			</WsEnvoi:seq00_CommencerEnvoi>
		  </s11:Body>
		</s11:Envelope>	
	</xsl:template>
</xsl:stylesheet>
