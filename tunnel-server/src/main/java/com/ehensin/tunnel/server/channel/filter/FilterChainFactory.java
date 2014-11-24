/*
 * Copyright 2013 The Ehensin Project
 *
 * The Ehensin Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ehensin.tunnel.server.channel.filter;

import java.util.List;

import com.ehensin.tunnel.server.ServerStartup;
import com.ehensin.tunnel.server.channel.IServerChannel;
import com.ehensin.tunnel.server.profile.FilterProfile;
import com.ehensin.tunnel.server.profile.FiltersProfile;

public class FilterChainFactory {
	private static final FilterChainFactory factory = new FilterChainFactory();
	private List<FilterProfile> filters;

	private FilterChainFactory() {
		FiltersProfile profile = ServerStartup.getStartup().getProfile()
				.getFilters();
		if (profile != null && profile.getFilters() != null
				&& profile.getFilters().size() > 0) {
			this.filters = profile.getFilters();
		}
	}

	public static FilterChainFactory getFactory() {
		return factory;
	}

	public FilterChain getFilterChain(IServerChannel channel) {
		return getFilterChain(filters, channel);
	}

	private FilterChain getFilterChain(List<FilterProfile> filters,
			IServerChannel channel) {
		/* insert internal filter */
		FilterChain chain = new FilterChain();
		chain.addLast(new LoggerFilter());
		chain.addLast(new KeepaliveFilter(channel));
		if (filters != null && filters.size() > 0) {
			for (FilterProfile p : filters) {
				String clazz = p.getClazz();
				IChannelFilter f;
				try {
					f = (IChannelFilter) Class.forName(clazz).newInstance();
					f.init(p.getName(), p.getDesc(), channel);
					chain.addLast(f);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		/*insert last protocal filter*/
		ProtocolFilter pf = new ProtocolFilter(channel);
		chain.addLast(pf);
		return chain;
	}
}
