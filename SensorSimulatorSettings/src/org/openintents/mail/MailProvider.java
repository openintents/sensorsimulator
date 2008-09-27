package org.openintents.mail;

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
import org.openintents.provider.Mail;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MailProvider extends ContentProvider {

	private SQLiteOpenHelper mOpenHelper;
	private static final String DATABASE_NAME="mail.db";
	private static final int DATABASE_VERSION=1;

	private static final String TABLE_ACCOUNTS="accounts";
	private static final String TABLE_MESSAGES="messages";
	private static final String TABLE_FOLDERS="folders";
	private static final String TABLE_SIGNATURES="signatures";
	private static final String TABLE_ATTACHMENTS="attachments";


	private static final String TAG="MailProvider";
	

	private static final int MAIL_ACCOUNTS=101;
	private static final int MAIL_ACCOUNT_ID=102;
	private static final int MAIL_MESSAGES=103;
	private static final int MAIL_MESSAGE_ID=104;
	private static final int MAIL_FOLDERS=105;
	private static final int MAIL_FOLDER_ID=106;
	private static final int MAIL_SIGNATURES=107;
	private static final int MAIL_SIGNATURE_ID=108;

	private static final UriMatcher URL_MATCHER;
	

	
	
	private static class DatabaseHelper extends SQLiteOpenHelper{

		DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

		
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG,"Creating table "+TABLE_ACCOUNTS);
			
			db.execSQL("CREATE TABLE "+TABLE_ACCOUNTS+" ("+
					Mail.Account._ID +" INTEGER PRIMARY KEY,"+
					Mail.Account._COUNT+" INTEGER,"+
					Mail.Account.TYPE+" STRING,"+
					Mail.Account.NAME+" STRING,"+
					Mail.Account.SEND_PROTOCOL+" STRING,"+
					Mail.Account.RECV_PROTOCOL+" STRING,"+
					Mail.Account.SEND_URI+" STRING,"+
					Mail.Account.RECV_URI+" STRING,"+
					Mail.Account.REPLY_TO+" STRING,"+
					Mail.Account.MAIL_ADDRESS+" STRING,"+
					Mail.Account.USERNAME+" STRING,"+
					Mail.Account.PASSWORD+" STRING,"+
					Mail.Account.DEFAULT_SIGNATURE+" STRING,"+
					Mail.Account.ACCOUNT_REFERENCE+" STRING"+
					");");
			
			Log.d(TAG,"Creating table"+TABLE_MESSAGES);
			
			db.execSQL("CREATE TABLE "+TABLE_MESSAGES+"("+
					Mail.Message._ID+" INTEGER PRIMARY KEY,"+
					Mail.Message._COUNT+" INTEGER,"+
					Mail.Message.ACCOUNT_ID+" INTEGER,"+
					Mail.Message.ACCOUNT_NAME+" STRING,"+
					Mail.Message.SUBJECT+" STRING,"+
					Mail.Message.SEND_DATE+" STRING,"+
					Mail.Message.RECV_DATE+" STRING,"+
					Mail.Message.CONTENT_TYPE+" STRING,"+
					Mail.Message.BODY+" STRING,"+
					Mail.Message.TO+" STRING,"+
					Mail.Message.CC+" STRING,"+
					Mail.Message.BCC+" STRING,"+
					Mail.Message.FROM+" STRING,"+
					Mail.Message.SENDER+" STRING,"+
					Mail.Message.REPLY_TO+" STRING,"+
					Mail.Message.MESSAGE_ID+" STRING,"+
					Mail.Message.IN_REPLY_TO+" STRING,"+
					Mail.Message.TAG_NAMES+" STRING,"+
					Mail.Message.HEADER+" STRING"+
					");"
					);
			
			db.execSQL("CREATE TABLE "+TABLE_FOLDERS+"("+
				Mail.Folders._ID+" INTEGER PRIMARY KEY,"+
				Mail.Folders._COUNT+" INTEGER"+
				");"
			);

			db.execSQL("CREATE TABLE "+TABLE_SIGNATURES+" ("+
				Mail.Signatures._ID+" INTEGER PRIMARY KEY,"+
				Mail.Signatures._COUNT+" INTEGER,"+
				Mail.Signatures.NAME+" STRING,"+
				Mail.Signatures.SIG+" STRING"+
				");"
			);

			db.execSQL("CREATE TABLE "+TABLE_ATTACHMENTS+" ("+
				Mail.Attachments._ID+" INTEGER PRIMARY KEY,"+
				Mail.Attachments._COUNT+" INTEGER,"+
				Mail.Attachments.ACCOUNT_ID+" INTEGER,"+
				Mail.Attachments.MAIL_ID+" INTEGER,"+
				Mail.Attachments.CONTENT_TYPE+" STRING,"+
				Mail.Attachments.CONTENT_TRANSFER_ENCODING+" STRING,"+
				Mail.Attachments.DATA+" BLOB,"+
				Mail.Attachments.STATUS+" STRING,"+
				Mail.Attachments.LOCAL_URI+" STRING,"+
				Mail.Attachments.SIZE+" INTEGER"+

				");"
			);


			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			Log.w(TAG,"upgrade not supported");
			//Log.v(TAG, "");
			
			
		}
		
		
		
	}//class helper

	@Override
	public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
	}


	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match=URL_MATCHER.match(uri);
		Log.d(this.TAG,"INSERT,URI MATCHER RETURNED >>"+match+"<<");
		long rowID=0;
		switch (match){
			case MAIL_ACCOUNTS:
				break;
			case MAIL_ACCOUNT_ID:
				break;
			case MAIL_MESSAGES:
				break;
			case MAIL_MESSAGE_ID:
				break;
			case MAIL_FOLDERS:
				break;
			case MAIL_FOLDER_ID:
				break;
			case MAIL_SIGNATURES:
				break;
			case MAIL_SIGNATURE_ID:
				break;
		}


		//TODO
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @author Zero
	 * @version 1.0
	 * @argument uri ContentURI NOT NULL
	 * @argument values ContentValues NOT NULL
	 * @return uri of the new item.
	 * 
	 */
	public Uri insert(Uri uri, ContentValues values) {
		int match=URL_MATCHER.match(uri);
		Log.d(this.TAG,"INSERT,URI MATCHER RETURNED >>"+match+"<<");
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowID=0;
		switch (match){
			case MAIL_ACCOUNTS:
				rowID=db.insert(TABLE_ACCOUNTS, "", values);			
				if (rowID > 0) {
					Uri nUri = ContentUris.withAppendedId(Mail.Account.CONTENT_URI,rowID);
					getContext().getContentResolver().notifyChange(nUri, null);
					return nUri;
				}
				throw new SQLException("Failed to insert row into " + uri);	
				//break;

			case MAIL_MESSAGES:

					rowID=db.insert(TABLE_MESSAGES, "", values);			
					if (rowID > 0) {
						Uri nUri = ContentUris.withAppendedId(Mail.Message.CONTENT_URI,rowID);
						getContext().getContentResolver().notifyChange(nUri, null);
						return nUri;
					}
					throw new SQLException("Failed to insert row into " + uri);	
				//break;
			case MAIL_FOLDERS:
					rowID=db.insert(TABLE_FOLDERS, "", values);			
					if (rowID > 0) {
						Uri nUri = ContentUris.withAppendedId(Mail.Folders.CONTENT_URI,rowID);
						getContext().getContentResolver().notifyChange(nUri, null);
						return nUri;
					}
					throw new SQLException("Failed to insert row into " + uri);					
			
			//break;
			case MAIL_SIGNATURES:
					rowID=db.insert(TABLE_SIGNATURES, "", values);			
					if (rowID > 0) {
						Uri nUri = ContentUris.withAppendedId(Mail.Signatures.CONTENT_URI,rowID);
						getContext().getContentResolver().notifyChange(nUri, null);
						return nUri;
					}
					throw new SQLException("Failed to insert row into " + uri);					
		}
		return null;
	}



	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		int match=URL_MATCHER.match(uri);
		Log.d(this.TAG,"INSERT,URI MATCHER RETURNED >>"+match+"<<");
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy=null;

		long rowID=0;
		switch (match){
			case MAIL_ACCOUNTS:
				qb.setTables(TABLE_ACCOUNTS);
				if (projection==null)
				{
					projection=Mail.Account.PROJECTION;
				}
				
				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = Mail.Account.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}
				break;
			case MAIL_ACCOUNT_ID:
				qb.setTables(TABLE_ACCOUNTS);
				//qb.setProjectionMap(FEED_PROJECTION_MAP);
				qb.appendWhere("_id=" + uri.getLastPathSegment());
				
				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = Mail.Account.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}
				break;
			case MAIL_MESSAGES:
				qb.setTables(TABLE_MESSAGES);
				if (projection==null)
				{
					projection=Mail.Message.PROJECTION;
				}
				
				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = Mail.Message.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}
				break;
			case MAIL_MESSAGE_ID:
				qb.setTables(TABLE_MESSAGES);
				//qb.setProjectionMap(FEED_PROJECTION_MAP);
				qb.appendWhere("_id=" + uri.getLastPathSegment());
				
				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = Mail.Message.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}
				break;
			case MAIL_FOLDERS:
				qb.setTables(TABLE_FOLDERS);
				if (projection==null)
				{
					projection=Mail.Folders.PROJECTION;
				}
				
				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = Mail.Folders.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}
				break;
			case MAIL_FOLDER_ID:
				qb.setTables(TABLE_FOLDERS);
				//qb.setProjectionMap(FEED_PROJECTION_MAP);
				qb.appendWhere("_id=" + uri.getLastPathSegment());
				
				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = Mail.Folders.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}
				break;
			case MAIL_SIGNATURES:
				qb.setTables(TABLE_SIGNATURES);
				if (projection==null)
				{
					projection=Mail.Signatures.PROJECTION;
				}
				
				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = Mail.Signatures.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}
				break;
			case MAIL_SIGNATURE_ID:
				qb.setTables(TABLE_SIGNATURES);
				//qb.setProjectionMap(FEED_PROJECTION_MAP);
				qb.appendWhere("_id=" + uri.getLastPathSegment());
				
				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = Mail.Signatures.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}
				break;
		}
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count=0;

		int match=URL_MATCHER.match(uri);
		Log.d(this.TAG,"INSERT,URI MATCHER RETURNED >>"+match+"<<");
		String rowID="";


		switch (match){
			case MAIL_ACCOUNTS:
				count= db.update(TABLE_ACCOUNTS, values, selection,selectionArgs);
				//getContext().getContentResolver().notifyChange(nUri, null);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			case MAIL_ACCOUNT_ID:
				rowID=uri.getPathSegments().get(1);
				count= db
					.update(TABLE_ACCOUNTS,
							values,
							"_id="+rowID
							+(!TextUtils.isEmpty(selection) ? " AND (" + selection
                            + ')' : ""),
							selectionArgs);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			case MAIL_MESSAGES:
				count= db.update(TABLE_MESSAGES, values, selection,selectionArgs);
				//getContext().getContentResolver().notifyChange(nUri, null);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			case MAIL_MESSAGE_ID:
				rowID=uri.getPathSegments().get(1);
				count= db
					.update(TABLE_MESSAGES,
							values,
							"_id="+rowID
							+(!TextUtils.isEmpty(selection) ? " AND (" + selection
                            + ')' : ""),
							selectionArgs);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			case MAIL_FOLDERS:
				count= db.update(TABLE_FOLDERS, values, selection,selectionArgs);
				//getContext().getContentResolver().notifyChange(nUri, null);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			case MAIL_FOLDER_ID:
				rowID=uri.getPathSegments().get(1);
				count= db
					.update(TABLE_FOLDERS,
							values,
							"_id="+rowID
							+(!TextUtils.isEmpty(selection) ? " AND (" + selection
                            + ')' : ""),
							selectionArgs);				
				getContext().getContentResolver().notifyChange(uri, null);
				break;

			case MAIL_SIGNATURES:
				count= db.update(TABLE_SIGNATURES, values, selection,selectionArgs);
				//getContext().getContentResolver().notifyChange(nUri, null);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			case MAIL_SIGNATURE_ID:
				rowID=uri.getPathSegments().get(1);
				count= db
					.update(TABLE_SIGNATURES,
							values,
							"_id="+rowID
							+(!TextUtils.isEmpty(selection) ? " AND (" + selection
                            + ')' : ""),
							selectionArgs);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			}
			return count;
		}


	static{
	
		URL_MATCHER=new UriMatcher(UriMatcher.NO_MATCH);
		
		URL_MATCHER.addURI("org.openintents.mail","accounts",MAIL_ACCOUNTS);
		URL_MATCHER.addURI("org.openintents.mail","accounts/#",MAIL_ACCOUNT_ID);
		URL_MATCHER.addURI("org.openintents.mail","messages",MAIL_MESSAGES);
		URL_MATCHER.addURI("org.openintents.mail","messages/#",MAIL_MESSAGE_ID);
		URL_MATCHER.addURI("org.openintents.mail","signatures",MAIL_SIGNATURES);
		URL_MATCHER.addURI("org.openintents.mail","signatures/#",MAIL_SIGNATURE_ID);
		URL_MATCHER.addURI("org.openintents.mail","folders",MAIL_FOLDERS);
		URL_MATCHER.addURI("org.openintents.mail","folders/#",MAIL_FOLDER_ID);

		
	}


}/*eoc*/