/* @()ISyncDataCacheListener.java
 *
 * (c) COPYRIGHT 2009-2013 Newcosoft INC. All rights reserved.
 * Newcosoft CONFIDENTIAL PROPRIETARY
 * Newcosoft Advanced Technology and Software Operations
 *
 * REVISION HISTORY:
 * Author             Date                   Brief Description
 * -----------------  ----------     ---------------------------------------
 * User            下午05:29:19                init version
 * 
 */
package com.ehensin.tunnel.client.cache;

import java.util.List;


/** 
 * <pre>
 * CLASS:
 * Describe class, extends and implements relationships to other classes.
 * 
 * RESPONSIBILITIES:
 * High level list of things that the class does
 * -) 
 * 
 * COLABORATORS:
 * List of descriptions of relationships with other classes, i.e. uses, contains, creates, calls...
 * -) class   relationship
 * -) class   relationship
 * 
 * USAGE:
 * Description of typical usage of class.  Include code samples.
 * 
 * 
 **/
public interface IDataCacheListener {
   public void syncDataPush(List<Object> data);
}
