<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
							  xmlns:s11="http://schemas.xmlsoap.org/soap/envelope/">
	<xsl:template match="/">
		<s11:Envelope >
		  <s11:Body>
		    <WsRecuperation:listerSuivi xmlns:WsRecuperation="http://10.89.156.154:9081/CedreWebService/services/WSSynchronisationSIPDARecuperation/">
			<xsl:choose>
				<xsl:when test = "/*/@method = 'GET'">
					<identifiantPda><xsl:value-of select="//path/component[2]"/></identifiantPda>
				</xsl:when>
				<xsl:when test = "/*/@method = 'POST'">
					<identifiantPda><xsl:value-of select="*/body/root/identifiantPda"/></identifiantPda>
				</xsl:when>
			</xsl:choose>
		</WsRecuperation:listerSuivi>
		  </s11:Body>
		</s11:Envelope>	
	</xsl:template>
</xsl:stylesheet>
