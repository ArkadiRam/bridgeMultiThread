
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;

/* 
 *
 *  Autor: Arkadzi Zaleuski
 *   Data: 24 styczen 2020 r.
 */

public class NarrowBridgeConsole{
	// Parametr TRAFFIC okre�la nat�enie ruchu bus�w.
	// Mo�e przyjmowa� warto�ci z przedzia�u [0, 5000]
	//    0 - bardzo ma�e nat�enie (nowy bus co 5500 ms)
	// 5000 - bardzo du�e nat�enie (nowy bus co 500 ms )
	public static int TRAFFIC = 1000;
		
}  // koniec klasy NarrowBridgeConsole


enum BusDirection{
	EAST,
	WEST;
	
	@Override
	public String toString(){
		switch(this){
		case EAST: return "W";
		case WEST: return "Z";
		}
		return "";
	}
} // koniec typu wyliczeniowego BusDirection



class NarrowBridge {
	int zmianaKierunku=0;
	BusDirection dir = BusDirection.WEST;
	int posY = 0;
	NarrowBridgeWindow frame;
	int mode = 1;
	// Lista wszystkich bus�w, kt�rych w�tki aktualnie dzia�aj�
	List<Bus> allBuses = new LinkedList<Bus>();
	
	// Lista bus�w (kolejka) oczekuj�cych na wjazd na most
	List<Bus> busesWaiting = new LinkedList<Bus>();
	
	// Lista bus�w poruszaj�cych si� aktualnie po mo�cie
	List<Bus> busesOnTheBridge = new LinkedList<Bus>();
	public void setFrame(NarrowBridgeWindow frame) {
		this.frame = frame;
	}
	public NarrowBridgeWindow getFrame() {
		return this.frame;
	}
	// Wydruk informacji o busach oczekuj�cych w kolejce oraz
	// aktualnie przeje�d�aj�cych przez most. 
	void printBridgeInfo(Bus bus, String message){
		synchronized(allBuses) {
		StringBuilder sb = new StringBuilder();
		StringBuilder queue = new StringBuilder();
		sb.append("Bus["+bus.id+"->"+bus.dir+"]  ");
		sb.append(message+"\n");
		//sb.append("           Na moscie: ");
		for(Bus b: busesOnTheBridge) queue.append(b.id + "  "); 
		frame.queueField.setText(queue.toString());
		queue.delete(0,queue.length());
		//sb.append("    Kolejka: ");
		for(Bus b: busesWaiting) queue.append(b.id + "  ");
		
		frame.bridgeField.setText(queue.toString());
		frame.area.append(sb.toString());
		}
	}
	
	public void setMode(int i) {
		this.mode = i;
	}
	// Procedura monitora, kt�ry wpuszcza busy na most
	synchronized void getOnTheBridge(Bus bus){
		// Prosty warunek wjazudu na most:
		// DOP�KI LISTA BUS�W NA MO�CIE NIE JEST PUSTA
		// KOLEJNY BUS MUSI CZEKA� NA WJAZD
		
		switch(mode) {
		case 1: {
			busesOnTheBridge.add(bus);
			printBridgeInfo(bus, "WJEZDZA NA MOST");
				break;			
		}
		case 2: {
			while( busesOnTheBridge.size()>=3){
				if(mode!=2) {
					break;
				}
	
				// dodanie busa do listy oczekuj�cych
				busesWaiting.add(bus);
				printBridgeInfo(bus, "CZEKA NA WJAZD");
				try {
					wait();
				} catch (InterruptedException e) { }
				
				busesWaiting.remove(bus);
			}
			// dodanie busa do listy jad�cych przez most
			if(mode!=2) {
				busesWaiting.remove(bus);
				getOnTheBridge(bus);
				break;
			}
			busesOnTheBridge.add(bus);
			printBridgeInfo(bus, "WJEZDZA NA MOST");
			break;			
	}
		case 3: {
			while(busesOnTheBridge.size()>=3 || ( busesOnTheBridge.size()>0 && dir!= bus.dir)  ){
				// dodanie busa do listy oczekuj�cych
				if(mode!=3) {
					//busesOnTheBridge.add(bus);
					//printBridgeInfo(bus, "WJEZDZA NA MOST");
						break;	
				}
			
				if(busesOnTheBridge.size()<3)
					notify();
				
				
				if(zmianaKierunku >= 11 && dir != BusDirection.WEST) {
					dir = BusDirection.WEST;
					zmianaKierunku=0;
				}
				else if(zmianaKierunku >=11) {
					dir = BusDirection.EAST;
					zmianaKierunku=0;
				}
				

				busesWaiting.add(bus);
				printBridgeInfo(bus, "CZEKA NA WJAZD");
				try {
					wait();
				} catch (InterruptedException e) { }
				// usuni�cie busa z listy oczekuj�cych.
				busesWaiting.remove(bus);
				
			}
			if(mode!=3) {
				busesWaiting.remove(bus);
				getOnTheBridge(bus);
				break;
			}
			// dodanie busa do listy jad�cych przez most
			zmianaKierunku++;
			busesOnTheBridge.add(bus);
			printBridgeInfo(bus, "WJEZDZA NA MOST");
			break;				
	}
		case 4: {
			while( !busesOnTheBridge.isEmpty()){
				if(mode!=4) {
					
					break;
				}
				
				// dodanie busa do listy oczekuj�cych
				busesWaiting.add(bus);
				printBridgeInfo(bus, "CZEKA NA WJAZD");
				try {
					wait();
				} catch (InterruptedException e) { }
				// usuni�cie busa z listy oczekuj�cych.
				
				busesWaiting.remove(bus);
			}
			if(mode!=4) {
				busesWaiting.remove(bus);
				getOnTheBridge(bus);
				break;
			}
			// dodanie busa do listy jad�cych przez most
			busesOnTheBridge.add(bus);
			printBridgeInfo(bus, "WJEZDZA NA MOST");
			break;			
	}
				
		}
		
	}
	
	// Procedura monitora, kt�ra rejestruje busy opuszczaj�ce most
	// i powiadamia inne busy oczekuj�ce w kolejce na wjazd
	 synchronized void getOffTheBridge(Bus bus){
		busesOnTheBridge.remove(bus);
		printBridgeInfo(bus, "OPUSZCZA MOST");
		// powiadomienie innych oczekujucyc
		if(mode == 1 && busesWaiting.size()>=2)
				notifyAll();
		else notify();
	}

}  // koniec klasy NarrowBridge




class Bus implements Runnable {
	BusVisualModel visualModel;
	// Sta�e okre�laj�ce minimalny i maksymalny czas
	// oczeiwania na nowych pasa�er�w.
	public static final int MIN_BOARDING_TIME = 1000;
	public static final int MAX_BOARDING_TIME = 10000;

	// Sta�a okre�laj�ca czas dojazdu busa do mostu.
	public static final int GETTING_TO_BRIDGE_TIME = 500;
	
	// Sta�a okre�laj�ca czas przejazdu przez most.
	public static final int CROSSING_BRIDGE_TIME = 3000;
	
	// Sta�a okre�laj�ca czas przjezdu od mostu do ko�cowego parkingu.
	public static final int GETTING_PARKING_TIME = 500;
	
	// Sta�a okre�laj�ca czas wysiadania pasa�er�w z busa
	public static final int UNLOADING_TIME = 1000;
	
	
	// Liczba wszystkich bus�w, kt�re zosta�u utworzone
	// od pocz�tku dzia�ania programu
	private static int numberOfBuses = 0;
	
	
	// Metoda usypia w�tek na podany czas w milisekundach
	public static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	// Metoda usypia w�tek na losowo dobrany czas
	// z przedzia�u [min, max) milsekund
	public static void sleep(int min_millis, int max_milis) {
		sleep(ThreadLocalRandom.current().nextInt(min_millis, max_milis));
	}
	
	
	// Referencja na obiekt reprezentuj�cy most.
	NarrowBridge bridge;
	NarrowBridgeWindow frame;
	// Unikalny identyfikator ka�dego busa. 
	// Jako identyfikator zostanie u�yty numer busa,
	// kt�ry zosta� utworzony od pocz�tku dzia�ania programu
	int id;
	
	// Kierunek jazdy busa nadany w spos�b losowy
	BusDirection dir;
	
	
	Bus(NarrowBridge bridge, NarrowBridgeWindow frame) throws IOException{
		this.bridge = bridge;
		this.frame = frame;
		this.id = ++numberOfBuses;
		bridge.posY++;
		
		if (ThreadLocalRandom.current().nextInt(0, 2) == 0)
			this.dir = BusDirection.WEST;
		else this.dir = BusDirection.EAST;
		this.visualModel = new BusVisualModel(5,bridge.posY*30,id,this.dir);
		if(bridge.posY>=20)
			bridge.posY = 1;
		synchronized(bridge.allBuses) {
		bridge.allBuses.add(this);
		}
	}
	
	// Wydruk w konsoli informacji o stanie busa
	synchronized void printBusInfo(String message){
		frame.area.append("Bus[" + id + "->"+dir+"]: " + message +"\n" );
	}
	
	
	// Symulacja oczekiwania na nowych pasa�er�w.
	synchronized void boarding() {
		printBusInfo("Czeka na nowych pasazerow");
		visualModel.stop();
		sleep(MIN_BOARDING_TIME, MAX_BOARDING_TIME);
		
	}

	// Symulacja dojazdu ze stacji pocz�tkowej do mostu
	synchronized void goToTheBridge() {
		visualModel.ride(this.dir,GETTING_TO_BRIDGE_TIME,100);
		printBusInfo("Jazda w strone mostu");
		sleep(GETTING_TO_BRIDGE_TIME);
		visualModel.stop();
	
		
	}

	// Symulacja przejazdu przez most
	synchronized void rideTheBridge(){
		printBusInfo("Przejazd przez most");
		visualModel.ride(this.dir,CROSSING_BRIDGE_TIME,150);
		
		sleep(CROSSING_BRIDGE_TIME);
		visualModel.stop();
	
		
	}

	// Symulacja przejazdu od mostu do ko�cowego parkingu
	void goToTheParking(){
		visualModel.ride(this.dir,GETTING_PARKING_TIME,120);
		printBusInfo("Jazda w strone koncowego parkingu");
		sleep(GETTING_PARKING_TIME);
	}
	
	// Symulacja opuszczenia pojazdu na przystanku ko�cowym
	synchronized void unloading(){
		visualModel.stop();
		printBusInfo("Rozladunek pasazerow");
		sleep(UNLOADING_TIME);

	}

	
	// Metoda realizuje "cykl �ycia" pojedynczego busa
	public  void run() {
		synchronized(bridge.allBuses) {
		bridge.allBuses.add(this);
		}
		
		
		// oczekiwanie na nowych pasa�er�w
		boarding();

		// jazda w kierunku mostu
		goToTheBridge();

		// 
		bridge.getOnTheBridge(this);

		// przejazd przez most
		rideTheBridge();

		bridge.getOffTheBridge(this);

		// jazda w kierunku parkingu ko�cowego
		goToTheParking();

		// wypuszczenie pasa�er�w
		unloading();
		synchronized(bridge.allBuses) {
		bridge.allBuses.remove(this);
		
		// koniec "�ycia" w�tku
		bridge.allBuses.remove(this);
		}
	}

}  // koniec klasy Bus