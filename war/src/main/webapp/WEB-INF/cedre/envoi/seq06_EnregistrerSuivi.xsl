<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
							  xmlns:s11="http://schemas.xmlsoap.org/soap/envelope/">
	<xsl:template match="/">
		<s11:Envelope >
		  <s11:Body>
		    <WsEnvoi:seq06_EnregistrerSuivi xmlns:WsEnvoi="http://10.89.156.154:9081/CedreWebService/services/WSSynchronisationPDASIEnvoi/">
			<donnees>
				<xsl:choose>
					<xsl:when test = "/*/@method = 'GET'">
						<codeMessage><xsl:value-of select="//path/component[2]"/></codeMessage>
						<codeRetour><xsl:value-of select="//path/component[3]"/></codeRetour>
						<content><xsl:value-of select="//path/component[4]"/></content>
						<identifiantPda><xsl:value-of select="//path/component[5]"/></identifiantPda>
						<message><xsl:value-of select="//path/component[6]"/></message>
						<sequence><xsl:value-of select="//path/component[7]"/></sequence>
					</xsl:when>
					<xsl:when test = "/*/@method = 'POST'">
						<codeMessage><xsl:value-of select="*/body/root/codeMessage"/></codeMessage>
						<codeRetour><xsl:value-of select="*/body/root/codeRetour"/></codeRetour>
						<content><xsl:value-of select="*/body/root/content"/></content>
						<identifiantPda><xsl:value-of select="*/body/root/identifiantPda"/></identifiantPda>
						<message><xsl:value-of select="*/body/root/message"/></message>
						<sequence><xsl:value-of select="*/body/root/sequence"/></sequence>
					</xsl:when>
				</xsl:choose>
			   </donnees>
			</WsEnvoi:seq06_EnregistrerSuivi>
		  </s11:Body>
		</s11:Envelope>	
	</xsl:template>
</xsl:stylesheet>
