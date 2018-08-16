/* Copyright 2014 predic8 GmbH, www.predic8.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */
package com.predic8.membrane.annot.generator;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.predic8.membrane.annot.model.MainInfo;
import com.predic8.membrane.annot.model.Model;

public class NamespaceInfo {

	private ProcessingEnvironment processingEnv;

	public NamespaceInfo(ProcessingEnvironment env) {
		this.processingEnv = env;
	}

	public void writeInfo(Model m) {
		try {
			FileObject o = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/membrane.namespaces");
			OutputStream oo = o.openOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(oo);
			try {
				Properties p = new Properties();
				int i = 1;

				for (MainInfo mi : m.getMains()) {
					String key = "schema" + (i++);
					p.put(key + "-targetNamespace", mi.getAnnotation().targetNamespace());
					p.put(key + "-outputPackage", mi.getAnnotation().outputPackage());
					p.put(key + "-outputName", mi.getAnnotation().outputName());
				}

				p.store(bos, "Autogenerated by " + getClass().getName());
			} finally {
				bos.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
