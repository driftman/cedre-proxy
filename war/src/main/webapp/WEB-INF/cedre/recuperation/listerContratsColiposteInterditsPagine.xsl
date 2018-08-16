<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
							  xmlns:s11="http://schemas.xmlsoap.org/soap/envelope/">
	<xsl:template match="/">
		<s11:Envelope >
		  <s11:Body>
		    <WsRecuperation:listerContratsColiposteInterditsPagine xmlns:WsRecuperation="http://10.89.156.154:9081/CedreWebService/services/WSSynchronisationSIPDARecuperation/">
			<xsl:choose>
				<xsl:when test = "/*/@method = 'GET'">
					<debutIntervalle><xsl:value-of select="//path/component[2]"/></debutIntervalle>
					<finIntervalle><xsl:value-of select="//path/component[3]"/></finIntervalle>
				</xsl:when>
				<xsl:when test = "/*/@method = 'POST'">
					<debutIntervalle><xsl:value-of select="*/body/root/debutIntervalle"/></debutIntervalle>
					<finIntervalle><xsl:value-of select="*/body/root/finIntervalle"/></finIntervalle>
				</xsl:when>
			</xsl:choose>
		</WsRecuperation:listerContratsColiposteInterditsPagine>
		  </s11:Body>
		</s11:Envelope>	
	</xsl:template>
</xsl:stylesheet>
