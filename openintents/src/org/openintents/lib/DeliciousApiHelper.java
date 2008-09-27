/*
 * Copyright (C) 2007-2008 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openintents.lib;

import java.net.URL;
import java.net.URLEncoder;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Scanner;
import org.xml.sax.*;
import org.xml.sax.ext.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;


//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.methods.GetMethod; 
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
//import org.apache.commons.httpclient.methods.*;

import org.apache.http.*;
import org.apache.http.conn.*;
import org.apache.http.impl.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.*;
import org.apache.http.util.*;



import android.util.Log;

/*
 * implements the ma.gnolia.com mirrod / del.icio.us  http api.
 * call Constructor wiht either MAGNOLIA_API or DELICOUS_API to determine what should be used.
 *
 *@author Ronan 'Zero' Schwarz zeroogle@gmail.com
 */
public class  DeliciousApiHelper{
	
	public static final String _TAG="DeliciousApiHelper";

	public static final String DELICIOUS_API="https://api.del.icio.us/v1/";
	public static final String MAGNOLIA_API="https://ma.gnolia.com/api/mirrord/v1/";
	
	private String mAPI;
	private String mUser;
	private String mPasswd;

	public DeliciousApiHelper(String api,String user,String passwd){
		this.mAPI=api;
		this.mUser=user;
		this.mPasswd=passwd;

		//init the authentication
		Authenticator.setDefault( new Authenticator() 
		{ 
		  @Override protected PasswordAuthentication getPasswordAuthentication() 
		  { 
			System.out.printf( "url=%s, host=%s, ip=%s, port=%s%n", 
							   getRequestingURL(), getRequestingHost(), 
							   getRequestingSite(), getRequestingPort() ); 
		 
			return new PasswordAuthentication( 
				DeliciousApiHelper.this.mUser, 
				DeliciousApiHelper.this.mPasswd.toCharArray()
				); 
		  } 
		} ); 

	}


	public String[] getTags() throws java.io.IOException{
		
		String[] result=null;
		String rpc=mAPI+"tags/get";
		Element tag;
		java.net.URL u=null;
		
		try
		{
			u=new URL(rpc);	
			
		}catch(java.net.MalformedURLException mu){
			System.out.println("Malformed URL>>"+mu.getMessage());
		}

		Document doc = null;

		try
		{
			javax.net.ssl.HttpsURLConnection connection=(javax.net.ssl.HttpsURLConnection)u.openConnection();
			//that's actualy pretty ugly to do, but a neede workaround for m5.rc15
			javax.net.ssl.HostnameVerifier v= new org.apache.http.conn.ssl.AllowAllHostnameVerifier();
			
			connection.setHostnameVerifier(v);
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			 doc=db.parse(connection.getInputStream());
		
		} catch (java.io.IOException ioe) {
			System.out.println("Error >>"+ioe.getMessage());		
			Log.e(_TAG,"Error >>"+ioe.getMessage());		
		
		} catch (ParserConfigurationException pce) {
			System.out.println("ERror >>"+pce.getMessage());
			Log.e(_TAG,"ERror >>"+pce.getMessage());
		} catch (SAXException se) {
			System.out.println("ERRROR>>"+se.getMessage());
			Log.e(_TAG,"ERRROR>>"+se.getMessage());

		}catch( Exception e )
		{
			Log.e(_TAG, "Error while excecuting HTTP method. URL is: " + u);
			System.out.println( "Error while excecuting HTTP method. URL is: " + u);
			e.printStackTrace();
		} 

		if (doc==null)
		{
			Log.e(_TAG,"document was null, check internet connection?");
			throw new java.io.IOException("Error reading stream >>"+rpc+"<<");
			
		}
		int tagsLen=doc.getElementsByTagName("tag").getLength();
		result=new String[tagsLen];
		for (int i=0;i<tagsLen ;i++ )
		{
			tag=(Element)doc.getElementsByTagName("tag").item(i);
			result[i]=new String(tag.getAttribute("tag").trim());
		}

		//System.out.println( new Scanner( u.openStream() ).useDelimiter( "\\Z" ).next() );
		return result;
	}

	public boolean addPost(String itemUrl,String description,String extended,String[] tags,boolean shared)throws java.io.IOException{
		
		String rpc=mAPI+"posts/add?";
		StringBuffer rpcBuf=new StringBuffer();
		StringBuffer tagsBuf=new StringBuffer();
		Element tag;
		URL u=null;

		String dateStamp;
		//TODO: timestamps

		if (description==null||description.equals(""))
		{
			description="no description";
		}
		if (extended==null)
		{
			extended=new String();
		}

		try
		{
			

			rpcBuf.append("&url="+itemUrl);
			rpcBuf.append("&description="+URLEncoder.encode(description));
			rpcBuf.append("&extendend="+URLEncoder.encode(extended));
			int tagsLen=tags.length;
			
			if (mAPI.equals(MAGNOLIA_API))
			{
				//Magnolia uses comma as tag separator,..
				for (int i=0;i<tagsLen;i++ )
				{
					tagsBuf.append(URLEncoder.encode(tags[i])+",");
				}
			}else if (mAPI.equals(DELICIOUS_API))
			{
				//while Delicious uses spaces
				for (int i=0;i<tagsLen;i++ )
				{
					tagsBuf.append(URLEncoder.encode(tags[i])+" ");
				}
			}

			rpcBuf.append("&tags="+tagsBuf.toString());
			if (shared)
			{
				rpcBuf.append("&shared=yes");
			}else{
				rpcBuf.append("&shared=no");
			}
			rpcBuf.append("&replace=no");

			}
		catch (Exception e)
		{
			Log.e(_TAG,"ERROR Encoding URL Parameters");
			e.printStackTrace();
		}

		rpc+=rpcBuf.toString();


		
		//rpc=rpcBuf.toString();
		System.out.println("\n"+rpc+"\n");
		try
		{
			u=new URL(rpc);	
		}catch(java.net.MalformedURLException mu){
			System.out.println("Malformed URL>>"+mu.getMessage());
		}

		String s="";
		try
		{
			javax.net.ssl.HttpsURLConnection connection=(javax.net.ssl.HttpsURLConnection)u.openConnection();
			//that's actualy pretty ugly to do, but a neede workaround for m5.rc15
			javax.net.ssl.HostnameVerifier v= new org.apache.http.conn.ssl.AllowAllHostnameVerifier();
			
			connection.setHostnameVerifier(v);
			
			//tru3 3v1l h4ack1ng ;)
			s=new Scanner( connection.getInputStream() ).useDelimiter( "\\Z" ).next();
	
		
		} catch (java.io.IOException ioe) {
			System.out.println("Error >>"+ioe.getMessage());		
			Log.e(_TAG,"Error >>"+ioe.getMessage());		
		
		}catch( Exception e )
		{
			Log.e(_TAG, "Error while excecuting HTTP method. URL is: " + u);
			System.out.println( "Error while excecuting HTTP method. URL is: " + u);
			e.printStackTrace();
		}
	
		if (s.equals("<result code=\"done\" />"))
		{
		//	System.out.println("YEA!");
			return true;
		}
		//System.out.println(s);

		return false;
	}

	public boolean replacePost(String itemUrl,String description,String extended,String[] tags,boolean shared){

		//TODO: replace postings.
		return false;
	}

}/*eoc*/
