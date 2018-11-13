/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.cds.transport;

import java.io.IOException;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Parses JSON that arrives in Page response echoing the requested sort order.
 * 
 * https://stackoverflow.com/questions/30974286/com-fasterxml-jackson-databind-jsonmappingexception-can-not-deserialize-instanc
 */
public class CustomSortDeserializer extends JsonDeserializer<Sort> {
	@Override
	public Sort deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		ArrayNode node = jp.getCodec().readTree(jp);
		Order[] orders = new Order[node.size()];
		int i = 0;
		for (JsonNode obj : node) {
			orders[i] = new Order(Direction.valueOf(obj.get("direction").asText()), obj.get("property").asText());
			i++;
		}
		return Sort.by(orders);
	}
}
