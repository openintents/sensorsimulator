package org.openintents.provider;

/*


 ****************************************************************************
 * Copyright (C) 2007-2008 OpenIntents.org                                  *
 *                                                                          *
 * Licensed under the Apache License, Version 2.0 (the "License");          *
 * you may not use this file except in compliance with the License.         *
 * You may obtain a copy of the License at                                  *
 *                                                                          *
 *      http://www.apache.org/licenses/LICENSE-2.0                          *
 *                                                                          *
 * Unless required by applicable law or agreed to in writing, software      *
 * distributed under the License is distributed on an "AS IS" BASIS,        *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and      *
 * limitations under the License.                                           *
 ****************************************************************************

OpenIntents defines and implements open interfaces for
improved interoperability of Android applications.

To obtain the current release, visit
  http://code.google.com/p/openintents/


*/


import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;
import android.provider.BaseColumns;
import java.util.*;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;

public class Mail{

	private static final String _TAG="MAIL";


	public static String INBOX="inbox";
	public static String OUTBOX="inbox";
	public static String SENTITEMS="sentitems";
	public static String TRASH="trash";
	public static String DRAFTS="drafts";


	public ContentResolver mContentResolver;

	public static final class Account implements BaseColumns{
		public static final Uri CONTENT_URI=
			Uri.parse("org.openintents.mail/accounts");

			public static final String DEFAULT_SORT_ORDER="";
			public static final String TYPE="type";

			public static final String NAME="name";

			public static final String SEND_PROTOCOL="send_protocol";

			public static final String RECV_PROTOCOL="recv_protocol";

			public static final String SEND_URI="send_uri";

			public static final String RECV_URI="recv_uri";

			public static final String REPLY_TO="reply_to";

			public static final String MAIL_ADDRESS="mail_address";

			public static final String USERNAME="username";

			public static final String PASSWORD="password";

			public static final String DEFAULT_SIGNATURE="default_signature";

			//if user/pass is empty, use this to look them up in 
			//central account storage. otherwise leaf column empty.
			public static final String ACCOUNT_REFERENCE="acc_ref";

			public static final String[] PROJECTION={
				_ID,
				_COUNT,
				TYPE,
				NAME,
				SEND_PROTOCOL,
				RECV_PROTOCOL,
				SEND_URI,
				RECV_URI,
				REPLY_TO,
				MAIL_ADDRESS,
				USERNAME,
				PASSWORD,
				ACCOUNT_REFERENCE,
				DEFAULT_SIGNATURE
			};


	};
	

	public static final class Message implements BaseColumns{

			public static final Uri CONTENT_URI=
				Uri.parse("org.openintents.mail/messages");

			public static final String DEFAULT_SORT_ORDER="";
			
			public static final String ACCOUNT_ID="account_id";

			public static final String ACCOUNT_NAME="account_name";

			public static final String SUBJECT="subject";


			public static final String SEND_DATE="send_date";

			public static final String RECV_DATE="recv_date";

			public static final String CONTENT_TYPE="content_type";

			public static final String BODY="body";

			public static final String TO="to";

			public static final String CC="cc";

			public static final String BCC="bcc";

			public static final String FROM="from";

			public static final String SENDER="sender";

			public static final String REPLY_TO="reply_to";

			public static final String MESSAGE_ID="message_id";

			public static final String IN_REPLY_TO="in_reply_to";

			public static final String TAG_NAMES="tag_names";

			public static final String HEADER="header";

			public static final String ATTACHMENTS="attachments";

			public static final String[] PROJECTION={
				_ID,
				_COUNT,
				ACCOUNT_ID,
				ACCOUNT_NAME,
				SUBJECT,
				SEND_DATE,
				RECV_DATE,
				CONTENT_TYPE,
				BODY,
				TO,
				CC,
				BCC,
				FROM,
				SENDER,
				REPLY_TO,
				MESSAGE_ID,
				IN_REPLY_TO,
				TAG_NAMES,
				HEADER,
				ATTACHMENTS
			};

	};
	

	public static final class Folders implements BaseColumns {
			public static final Uri CONTENT_URI=
				Uri.parse("org.openintents.mail/folders");

			public static final String DEFAULT_SORT_ORDER="";

			public static final String ACCOUNT_ID="account_id";

			public static final String FOLDER_NAME="folder_name";

			public static final String FOLDER_PARENT="folder_parent";

			public static final String FOLDER_CHILDS="folder_childs";

			public static final String FOLDER_COMPLETE_PATH="folder_complete_path";


			public static final String[] PROJECTION={
				ACCOUNT_ID,
				FOLDER_NAME,
				FOLDER_PARENT,
				FOLDER_CHILDS
			};

	};



	public static final class Signatures implements BaseColumns{
			public static final Uri CONTENT_URI=
				Uri.parse("org.openintents.mail/signatures");
		public static final String DEFAULT_SORT_ORDER="";
		//Name of signature, eg "Default"
		public static final String NAME="name";

		//the Signature itself
		public static final String SIG="sig";

		public static final String[] PROJECTION={
			NAME,
			SIG
		};
	};

	public static final class Attachments implements BaseColumns{
			public static final Uri CONTENT_URI=
				Uri.parse("org.openintents.mail/attachments");
		public static final String DEFAULT_SORT_ORDER="";

		public static final String ACCOUNT_ID="account_id";

		public static final String MAIL_ID="mail_id";

		public static final String CONTENT_TYPE="content_type";

		public static final String CONTENT_TRANSFER_ENCODING="content_transfer_encoding";

		public static final String DATA="data";

		public static final String STATUS="status";
		
		public static final String LOCAL_URI="local_uri";

		public static final String SIZE="size";

		public static final String STATUS_DOWNLOADED="downloaded";
		public static final String STATUS_UPLOADED="uploaded";
		public static final String STATUS_SAVED_LOCAL="saved";
		public static final String STATUS_WILL_DOWNLOAD="w_down";
		public static final String STATUS_WILL_UPLOAD="w_up";


		public static final String[] PROJECTION={
			ACCOUNT_ID,
			MAIL_ID,
			CONTENT_TYPE,
			CONTENT_TRANSFER_ENCODING,
			DATA,
			STATUS,
			LOCAL_URI,
			SIZE
		};
	
	};

	

		public static Cursor getInboxForAccount(String AccountName){return null;}
		
		public static Cursor getOutboxForAccount(String AccountName){return null;}

		public static Cursor getSentItemsForAccount(String AccountName){return null;}

		public static Cursor getTrashForAccount(String AccountName){return null;}
	
		public static Cursor getDraftsForAccount(String AccountName){return null;}





}