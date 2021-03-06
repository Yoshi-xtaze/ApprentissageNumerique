package tp4.hmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * classe qui permet de mod�liser un HMM
 * 
 * un HMM est d�crit par - un nombre d'�tats - un nombre d'observations
 * possibles - une matrice de transition - une matrice d'observation
 * 
 * @author vthomas
 * 
 */
public class HMM {

	/**
	 * la classe ou est stockee la matrice de transition P( S_t / S_t-1)
	 */
	public ProcessusTransition matriceTransition;

	/**
	 * la classe ou est stockee la matrice d'observation P(O_t / O_t-1)
	 */
	public ProcessusObservation matriceObservation;

	/**
	 * constructeur vide
	 */
	public HMM() {
		// creation des lois de probabilite
		this.matriceObservation = new ProcessusObservation();
		this.matriceTransition = new ProcessusTransition();
	}

	/**
	 * faire evoluer etat en fonction de l'�tat de d�part
	 * 
	 * @return etat d'arrivee
	 */
	public State evoluerEtat(State depart) {
		return (this.matriceTransition.evoluerEtat(depart));
	}

	/**
	 * tire une observation en fonction de l'etat
	 * 
	 * @param depart
	 *            etat a partir duquel on observe
	 * @return Observation faite
	 */
	public Observation observer(State depart) {
		return (this.matriceObservation.observer(depart));
	}

	/**
	 * predire un belief
	 * 
	 * @param densite
	 *            � instant t-1
	 * @return densite � instant t
	 */
	public Distribution<State> maJTransition(Distribution<State> d) {
		//System.out.println("maJTransition");
		Distribution<State> res = new Distribution<>();
		double proba;
		for (State s1 : this.matriceTransition.transition.keySet()) {
			proba = 0;
			for (State s2 : this.matriceTransition.transition.keySet()) {
				System.out.println(proba+" + ("+this.matriceTransition.probaTransition(s2, s1)+" * "+d.getProba(s2)+")");
				proba += this.matriceTransition.probaTransition(s2, s1)*d.getProba(s2);
			}
			res.setProba(s1, proba);
			System.out.println("\n"+s1.toString()+" -> "+proba+"\n");
		}
		res.normalise();
		return res;
	}

	/**
	 * permet de mettre � jour un belief par rapport � une observation
	 * 
	 * @param d
	 *            la densite actuelle
	 * @param o
	 *            l'observation percue
	 * @return la densite mise � jour
	 */
	public Distribution<State> MaJObservation(Distribution<State> d, Observation o) {
		Distribution<State> res = new Distribution<>();
		double proba;
		double facteurNormalisation = 0;
		for (State s : this.matriceObservation.observation.keySet()) {
			facteurNormalisation += this.matriceObservation.probaObservation(o, s)*d.getProba(s);
		}
		for (State s : this.matriceObservation.observation.keySet()) {
			Distribution<Observation> mapObs = this.matriceObservation.observation.get(s);
			//System.out.print("Etat "+s+" : Correction = "+this.matriceObservation.probaObservation(o, s));
			//System.out.print(" Prediction = "+d.getProba(s));
			//System.out.println(" Facteur normalisation = "+mapObs.calculNorme());
			proba = (double)(d.getProba(s)*this.matriceObservation.probaObservation(o, s))/facteurNormalisation;
			res.setProba(s, proba);
		}
		res.normalise();
		return res;
	}

	/**
	 * m�thode qui propage la densite en prenant en compte l'observation
	 * 
	 * @param o
	 *            observation recue
	 * @return nouvelle densite
	 */
	public Distribution<State> propagation(Distribution<State> initiale, Observation o) {
		Distribution<State> distribApres = new Distribution<>();
		distribApres = maJTransition(initiale);
		distribApres = MaJObservation(distribApres, o);
		return distribApres;
	}

	/**
	 * m�thode qui calcule la densite de probabilit� apr�s t pas de temps en
	 * prenant en compte la liste des observations re�ues
	 * 
	 * @param d
	 *            la densite initiale
	 * @param l
	 *            la liste des obsrevations (la taille correspond au nombre de
	 *            pas de temps)
	 * @return la nouvelle densit�
	 */
	public Distribution<State> filtrage(Distribution<State> d, ArrayList<Observation> l) {
		Distribution<State> distribRes = new Distribution<>();
		distribRes = d;
		//System.out.println(distribRes);
		for (Observation observation : l) {
			distribRes = maJTransition(distribRes);
			distribRes = MaJObservation(distribRes, observation);
			//System.out.println(distribRes.toString());
		}
		return distribRes;
	}

	/**
	 * m�thode qui retourne la probabilit� d'observer une certaine sequence
	 * d'observations
	 * 
	 * @param l
	 *            liste des observations vues
	 * @param d
	 *            densit� initiale sur les �tats
	 * 
	 * @return probabilite de voir la sequence pass�e en parametre
	 */
	public double probaSequence(Distribution<State> initiale, ArrayList<Observation> l) {
		// **********************************************************************************
		// a faire par etudiants
		// je vous conseille de commencer par �crire des commentaires
		// pour structurer votre code avant de coder reellement la m�thode
		// **********************************************************************************

		throw new Error(); // ** A COMPLETER **
	}

	/**
	 * 
	 * methode qui cherche la trajectoire expliquant au mieux les observations
	 * algorithme viterbi utilisant la programmation dynamique
	 * 
	 * deux phases --> une phase aller pour propagare les probabilit�s --> une
	 * phase retour pour remonter le meilleur chemin
	 * 
	 * @param obs
	 *            la s�quence d'observations
	 * @return la s�quence d'�tats expliquant au mieux la s�quence
	 *         d'observations
	 */
	public ArrayList<State> viterbi(ArrayList<Observation> obs, Distribution<State> initiale) {
		// **********************************************************************************
		// a faire par etudiants
		// je vous conseille de commencer par �crire des commentaires
		// pour structurer votre code avant de coder reellement la m�thode
		// **********************************************************************************

		// phase aller - propagation des probabilit�s
		// id�e evaluer max_S P(S|O)
		// approche recursive f(s)=max_{s1,s2,..sk-1} P(s1...sk-1 sk=s|O_1..o_k)
		// = P(o_k|s_k). max_s_{k-1} (T(s_k|s_k-1). f(s_k-1))
		// se souvenir du sk-1 � chaque iteration pour pouvoir remonter

		// donn�es
		// proba stock� dans un tableau qui associe � t et s une valeur
		// ancetre stocke dans un tableau qui a t et a s associe un parent
		
		ArrayList<State> res = new ArrayList<State>();
		int nbIteration = obs.size();
		int nbEtats = 8; // A voir comment mieux faire
		double maxProba = 0;
		State s = null;
		State etatAncetre = null;
		double[][] prob = new double[nbIteration][nbEtats];
		State[][] ancetre = new State[nbIteration][nbEtats];
		//initiale = maJTransition(initiale);
		
		//System.out.println("Distribution initiale : "+initiale.toString()+"\n\n");
		
		for (int i = 0; i < nbIteration; i++) {
			for (int j = 0; j < nbEtats; j++) {
				s = new State(j);
				//System.out.print(s.toString()+" ");
				//System.out.print(initiale.getProba(s)+" -> ");
				//System.out.print(this.matriceObservation.probaObservation(obs.get(i), s));
				//System.out.println(" |"+this.matriceObservation.probaObservation(obs.get(i), s)*initiale.getProba(s)+"| ");
				prob[i][j] = this.matriceObservation.probaObservation(obs.get(i), s)*initiale.getProba(s);
			}
		
		
			//System.out.println("\n");
		
			for (int j = 0; j < nbEtats; j++) {
				maxProba = 0;
				s = new State(j);
				for (State clef : this.matriceTransition.transition.keySet()) {
					//System.out.println(this.matriceTransition.transition.get(clef).getProba(s)+" * "+prob[i][clef.num_etat]);
					if(this.matriceTransition.transition.get(clef).getProba(s)*prob[i][clef.num_etat] > maxProba){
						maxProba = this.matriceTransition.transition.get(clef).getProba(s)*prob[i][clef.num_etat];
						etatAncetre = clef;
					}
				}
				//System.out.println();
				//System.out.println(s.toString()+" -> "+maxProba+"   "+etatAncetre);
				//System.out.println();
				initiale.setProba(s, maxProba);
				ancetre[i][j] = etatAncetre;
			}
			//System.out.println("\n"+initiale.toString()+"\n");
			
		}
		
		/*System.out.println("Les carr�s : \n");
		for (int i = 0; i < prob[0].length; i++) {
			for (int j = 0; j < prob.length; j++) {
				System.out.print(prob[j][i]+" ");
			}
			System.out.println();
		}
		
		System.out.println("\n\n");
		
		System.out.println("Ancetres : \n");
		for (int i = 0; i < ancetre[0].length; i++) {
			for (int j = 0; j < ancetre.length; j++) {
				System.out.print(ancetre[j][i]+" ");
			}
			System.out.println();
		}
		
		System.out.println("\n\n");*/
		
		double max = 0;
		int indice = 0;
		for (int i = 0; i < prob[0].length; i++) {
			if(prob[prob.length-1][i] > max){
				max = prob[prob.length-1][i];
				indice = i;
			}
		}
		
		//System.out.println(max+"  "+indice);
		
		for (int i = obs.size()-1; i >= 0; i--) {
			res.add(ancetre[i][indice]);
			indice = ancetre[i][indice].num_etat;
		}
		
		return res;
	}

}
