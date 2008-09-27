package org.openintents.locations;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

public class MockLocationService extends Service {
  
  private static String LOG_TAG = "Mock Location Provider";

  public final static String PROVIDER_ID = LocationManager.GPS_PROVIDER;
  private int positionOffset;
  private static String BUNDLE_OFFSET_KEY = "klkj9389u5";
  
  /*
   * a hard-coded set of coordinates, replace this with whatever data set you
   * want or adapt this class to use a file or something else as its coordinate
   * source
   * in addition, a time can be set
   */
  private double[] coords =  { 
		  40.67966166652983, -74.00394916534424, 0, 
		  40.67972675755375, -74.00515079498291, 10000, 
		  40.68047529976021, -74.00639533996582, 10000, 
		  40.68008475704905, -74.00708198547363, 10000, 
		  40.68099601981583, -74.0084981918335, 10000, 
		  40.67891311518678, -74.01111602783203, 10000,
		  40.67972675755375, -74.01218891143799, 10000, 
		  40.67930366476232, -74.01278972625732, 10000, 
		  40.6801498476598, -74.01429176330566, 10000, 
		  40.68018239294134, -74.01497840881348, 10000, 
		  40.67985693941085, -74.0160083770752, 10000, 
		  40.67949893869203, -74.01673793792725, 10000, 
		  40.67894566107212, -74.01738166809082, 10000, 
		  40.67839237886013, -74.01776790618896, 10000, 
		  40.67796927759955, -74.01819705963135, 10000, 
		  40.67748108050053, -74.01823997497559, 10000, 
		  40.676992879826386, -74.01806831359863, 10000, 
		  40.67653722263828, -74.01785373687744, 10000, 
		  40.67598392043729, -74.01768207550049, 10000, 
		  40.675495708799446, -74.01738166809082, 10000, 
		  40.674747110677785, -74.0172529220581, 10000, 
		  40.67445417868994, -74.0172529220581, 10000, 
		  40.67403105243536, -74.01708126068115, 10000, 
		  40.67373811730146, -74.01660919189453, 10000, 
		  40.67406360070414, -74.01592254638672, 10000, 
		  40.6745518228289, -74.01549339294434, 10000, 
		  40.67477965859698, -74.01467800140381, 10000, 
		  40.674421630611846, -74.01424884796143, 10000, 
		  40.673933407533724, -74.01442050933838, 10000, 
		  40.673640471970806, -74.01412010192871, 10000, 
		  40.673705568873785, -74.01317596435547, 10000, 
		  40.67351027797417, -74.01266098022461, 10000, 
		  40.67298949944538, -74.01257514953613, 10000, 
		  40.67266401079927, -74.01261806488037, 10000, 
		  40.672371069659285, -74.01283264160156, 10000, 
		  40.67181773288338, -74.01283264160156, 10000, 
		  40.67145968899321, -74.01261806488037, 10000, 
		  40.67106909346583, -74.01248931884766, 10000, 
		  40.670873794844134, -74.01266098022461, 10000, 
		  40.670694770605074, -74.0128755569458, 10000, 
		  40.67054829587018, -74.01326179504395, 1000, 
		  40.67041809583572, -74.01373386383057, 1000, 
		  40.67028789554703, -74.01414155960083, 1000, 
		  40.670141419918366, -74.0142273902893, 1000, 
		  40.67007631953568, -74.01416301727295, 1000, 
		  40.66994611857968, -74.01409864425659, 1000, 
		  40.66986474285306, -74.01403427124023, 1000, 
		  40.66997866884251, -74.01373386383057, 1000, 
		  40.67006004443011, -74.01330471038818, 1000, 
		  40.67020652023749, -74.0128755569458, 1000, 
		  40.670369270757256, -74.01244640350342, 1000, 
		  40.67053202087979, -74.01180267333984, 1000, 
		  40.67040182081353, -74.01113748550415, 1000, 
		  40.67023907037323, -74.01083707809448, 1000, 
		  40.67006004443011, -74.01032209396362, 1000, 
		  40.66994611857968, -74.00965690612793, 1000, 
		  40.66994611857968, -74.00942087173462, 1000, 
		  40.669929843442304, -74.00890588760376, 1000, 
		  40.67007631953568, -74.00824069976807, 1000, 
		  40.67040182081353, -74.00781154632568, 1000, 
		  40.67102026886402, -74.0074896812439, 1000, 
		  40.67170381003596, -74.00736093521118, 1000, 
		  40.67248499136667, -74.00742530822754, 1000, 
		  40.672680285269315, -74.00660991668701, 1000, 
		  40.67285930417761, -74.00598764419556, 1000, 
		  40.672680285269315, -74.00549411773682, 1000, 
		  40.67255008939778, -74.00510787963867, 1000, 
		  40.672387344200835, -74.00487184524536, 1000, 
		  40.67232224601083, -74.00450706481934, 1000, 
		  40.672224598606654, -74.00403499603271, 1000, 
		  40.67212695105948, -74.00364875793457, 1000, 
      };
  private PositionProvider updateThread = null;
  
  @Override
  public IBinder onBind(Intent arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
    LocationManager lm = (LocationManager)this.getSystemService(LOCATION_SERVICE);
    
    // check if we're resuming from a previous instantiation
    positionOffset = intent.getIntExtra(BUNDLE_OFFSET_KEY, 0);
    
    // okay, now we're ready to start sending updates, make sure we're enabled
    if (lm.isProviderEnabled(PROVIDER_ID)){
    	// oh no
    	Log.e(LOG_TAG, "provider " + PROVIDER_ID + " not enabled");
    }
    updateThread = new PositionProvider(lm);
    Log.i(LOG_TAG, "Starting position sender thread.");
    updateThread.start();
  }
  
  @Override
  public void onDestroy() {
    // TODO Auto-generated method stub
    super.onDestroy();
    Log.i(LOG_TAG, "Location provider service being killed");
    
    // allow the provider time to shut down
    if (updateThread != null) {
      updateThread.stop = true;
      while(!updateThread.isStopped);
    }
  }


  @Override
  public boolean onUnbind(Intent intent) {
    // TODO Auto-generated method stub
    return super.onUnbind(intent);
  }


  private class PositionProvider extends Thread {
    public boolean stop = false;
    public boolean isStopped = true;
    private LocationManager locationService = null;
    
    public PositionProvider (LocationManager lm) {
      super();
      locationService = lm;
    }
    
    public void run () {
      isStopped = false;
      Location l = new Location(PROVIDER_ID);
      
      long lastTime = System.currentTimeMillis();
      
      // loop around our location dataset until we're told to stop
      while (!stop) {
        if (positionOffset * 3 + 4 >= coords.length) {
          positionOffset = 0;
        }
        
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastTime < coords[positionOffset * 3 + 2]) {
        	// don't use the next coordinate - choose one between
        	if (positionOffset == 0) {
        		setLocation(l, 0, 0, coords[positionOffset * 3], coords[positionOffset * 3+1], 1);
        	} else {
        		setLocation(l, coords[positionOffset * 3-3], coords[positionOffset * 3-2], coords[positionOffset * 3], coords[positionOffset * 3+1], (currentTime - lastTime)/coords[positionOffset * 3 + 2]);
        	}
        } else {
        	// use the next coordinate as base and choose between
        	
        	
        	setLocation(l, coords[positionOffset * 3], coords[positionOffset * 3+1], coords[positionOffset * 3+3], coords[positionOffset * 3+4], (currentTime - lastTime - coords[positionOffset * 3+2])/coords[positionOffset * 3 + 5]);
        	++positionOffset;
        	lastTime = currentTime;
        	
        }        
        // set the time in the location. If the time on this location matches
        // the time on the one in the previous set call, it will be ignored
        l.setTime(System.currentTimeMillis());
        
        // TODO not supported in SDK 1.0
        // locationService.setTestProviderLocation(PROVIDER_ID, l);
        
        try {
          Thread.sleep(500l);
        } catch (Exception e) {
          // we don't really care if our sleep was interrupted.
        }
        
      }
      isStopped = true;
    }
    
    private void setLocation(Location location, double previousLatitude, double previousLongitude, double latitude, double longitude, double percentage) {
    	location.setLatitude(previousLatitude* (1d-percentage)+ latitude*percentage);
    	location.setLongitude(previousLongitude* (1d-percentage)+ longitude*percentage);
    }
  }
}