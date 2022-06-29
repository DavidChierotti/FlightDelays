package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	private Graph<Airport,DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer,Airport> idMap;
	
	public Model() {
		dao=new ExtFlightDelaysDAO();
		idMap=new HashMap<Integer,Airport>();
	}
	
	
	public void creaGrafo(int x) {
		grafo= new SimpleWeightedGraph<Airport,DefaultWeightedEdge>(DefaultWeightedEdge.class);
	    //aggiungere i vertici	
	   Graphs.addAllVertices(this.grafo,dao.getVertici(x, idMap));
	   
	   //aggiungere gli archi
	   for(Rotta r:dao.getRotte(idMap)) {
		   if(this.grafo.containsVertex(r.getA1())&&this.grafo.containsVertex(r.getA1())) {
		   DefaultWeightedEdge edge=this.grafo.getEdge(r.getA1(),r.getA2());//guardo se c'è un arco tra i due 
		                                                                    //aereoporti
		   if(edge==null) {
			   Graphs.addEdgeWithVertices(this.grafo,r.getA1(),r.getA2(),r.getnVoli());//guarda se c'è arco tra due vertici
	//se non c'è arco tra due vertici lo creo		   
		   }
		   else {
			  double pesoVecchio=this.grafo.getEdgeWeight(edge);
			  double pesoNuovo=pesoVecchio+r.getnVoli();
			  this.grafo.setEdgeWeight(edge, pesoNuovo);
    // quando trovo l'arco inverso, essendo il grafo non orientato, somma il numero di voli così da avere il totale
		   }
		 }
	   }
	}
	
	public List<Airport> getVertici(){
		List<Airport> vertici=new ArrayList(this.grafo.vertexSet());
		Collections.sort(vertici);
		return vertici;
	}
	
	
	
	
	public List<Airport> getPercorso(Airport a1,Airport a2){
		List<Airport> percorso=new ArrayList<Airport>();
		BreadthFirstIterator<Airport,DefaultWeightedEdge> it=new BreadthFirstIterator<>(this.grafo,a1);
		
		
		Boolean trovato=false;
		//visita il grafo
		while(it.hasNext()) {
			Airport visitato=it.next();
			if(visitato.equals(a2))
				trovato=true;
		}
		if(trovato==true) {
		//ottengo il percorso(metodo alternativo al traversal listener)
		percorso.add(a2); //parto dalla destinazione
		Airport step=it.getParent(a2);
		while(!step.equals(a1)) {
			percorso.add(0,step);// con lo zero aggiunge in testa
			step=it.getParent(step);
		}
		percorso.add(0,a1);
		return percorso;
		}
		else return null;
		
		
	}
	
	
	
	
	
	
	
	
}
