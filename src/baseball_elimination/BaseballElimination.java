import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * BaseballElimination.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on	
 */

public class BaseballElimination {
	
	private class BaseballTeam{
		private String name;
		private int wins, losses, remaing;
		private boolean isElimated;
		private ArrayList<String> certificateOfElimation;
		
		public BaseballTeam(String name, int wins, int losses, int remaining) {
			this.name = name;
			this.wins = wins;
			this.losses = losses;
			this.remaing = remaining;
			isElimated = false;
		}
		
		public String getName() {
			return name;
		}
		
		public boolean isElimated() {
			return isElimated;
		}
		
		public void setElimated(boolean isElimated) {
			this.isElimated = isElimated;
		}
		
		public void addElimationTeam(String teamName) {
			if (certificateOfElimation == null) {
				certificateOfElimation = new ArrayList<>();
			}
			certificateOfElimation.add(teamName);
		}
		
		public ArrayList<String> getCertificateOfElimation() {
			return certificateOfElimation;
		}
	}

	private BaseballTeam[] teams;
	private ArrayList<String> teamNames;
	private int[][] againstMatches;
	
	private int tmpCurTeamMaxPossibleWin;
	private int tmpAgainstTeamIndexes[];
	
	public BaseballElimination(String filename) {
		In file = new In(filename);
		int noOfTeams = file.readInt();
		teams = new BaseballTeam[noOfTeams];
		teamNames = new ArrayList<>(noOfTeams);
		againstMatches = new int[noOfTeams][];
		for(int i = 0; i < noOfTeams; i++) {
			againstMatches[i] = new int[noOfTeams];
			String teamName = file.readString();
			BaseballTeam newTeam = new BaseballTeam(teamName, file.readInt(), file.readInt(), file.readInt());
			for (int j = 0; j < noOfTeams; j++) {
				int noOfMatches = file.readInt();
				againstMatches[i][j] = noOfMatches;
			}
			teams[i] = newTeam;
			teamNames.add(teamName);
		}
		performTrivialElimination();
		performNontrivialElimination();
	}
	
	public Iterable<String>teams(){
		return new ArrayList<String>(teamNames);
	}
	
	private void checkIfValidTeamName(String team){
		if (!teamNames.contains(team)) {
			throw new IllegalArgumentException("Invalid team name");
		}
	}
	
	public int wins(String team) {
		checkIfValidTeamName(team);
		return teams[teamNames.indexOf(team)].wins;
	}
	
	public int losses(String team) {
		checkIfValidTeamName(team);
		return teams[teamNames.indexOf(team)].losses;
	}
	
	public int remaining(String team) {
		checkIfValidTeamName(team);
		return teams[teamNames.indexOf(team)].remaing;
	}
	
	public int against(String team1, String team2) {
		checkIfValidTeamName(team1);
		checkIfValidTeamName(team2);
		return againstMatches[teamNames.indexOf(team1)][teamNames.indexOf(team2)];
	}
	
	public boolean isEliminated(String team) {
		checkIfValidTeamName(team);
		return teams[teamNames.indexOf(team)].isElimated;
	}
	
	public Iterable<String> certificateOfElimination(String team){
		checkIfValidTeamName(team);
		return teams[teamNames.indexOf(team)].getCertificateOfElimation();
	}
	
	public int numberOfTeams() {
		return teamNames.size();
	}
	
	private Comparator<BaseballTeam> winsComparator = new Comparator<BaseballElimination.BaseballTeam>() {
		@Override
		public int compare(BaseballTeam o1, BaseballTeam o2) {
			return o2.wins - o1.wins;
		}
	}; 
	
	private void performTrivialElimination() {
		BaseballTeam[] sortedTeams = Arrays.copyOf(teams,	 teams.length); 
		Arrays.sort(sortedTeams, winsComparator);
		for (BaseballTeam baseballTeam : teams) {
			for (BaseballTeam sortedTeam : sortedTeams) {
				if (baseballTeam.getName().equals(sortedTeam.getName())) {
					continue;
				}
				if (baseballTeam.wins + baseballTeam.remaing < sortedTeam.wins) {
					baseballTeam.setElimated(true);
					baseballTeam.addElimationTeam(sortedTeam.getName());
				}else {
					break;
				}

			}
		}
	}
	
	private FlowNetwork createFlowNetwork(BaseballTeam team) {
		int curTeamPos = teamNames.indexOf(team.name);
		tmpCurTeamMaxPossibleWin = 0;
		
		int teamMaxPossibleWin = team.wins + team.remaing;
		int againstMatchTeamsSize = teams.length - 1;
		int matchesAgainsts = (againstMatchTeamsSize * (againstMatchTeamsSize-1))/2;
		int noOfNodes = 2 + matchesAgainsts + againstMatchTeamsSize;
		
		tmpAgainstTeamIndexes = new int[againstMatchTeamsSize];
		for (int i = 0, index = 0; i < teams.length; i++) {
			if (i == curTeamPos) {
				continue;
			}
			tmpAgainstTeamIndexes[index++] = i;  
		}
		
		int teamsNodeStart = 1 + matchesAgainsts;
		FlowNetwork network = new FlowNetwork(noOfNodes);
		
		int curMatchNode = 1;
		for (int agd = 0; agd < againstMatchTeamsSize; agd++) {
			int i = tmpAgainstTeamIndexes[agd];
			for (int ind = 0; ind < againstMatchTeamsSize; ind++){
				int j = tmpAgainstTeamIndexes[ind];
				if (j > i) {
					int matches = againstMatches[i][j];
					tmpCurTeamMaxPossibleWin += matches;
					network.addEdge(new FlowEdge(0, curMatchNode, matches));
					network.addEdge(new FlowEdge(curMatchNode, teamsNodeStart+ind, Double.POSITIVE_INFINITY));
					network.addEdge(new FlowEdge(curMatchNode, teamsNodeStart+agd, Double.POSITIVE_INFINITY));
					curMatchNode++;
				}
			}
			int weight = teamMaxPossibleWin - teams[i].wins;
			network.addEdge(new FlowEdge(teamsNodeStart+agd, noOfNodes-1, weight));
		}
		return network;
	}
	
	private void performNontrivialElimination() {
		if (teams.length > 2) {
			for (BaseballTeam curTeam : teams) {
				if(!curTeam.isElimated()) {
					FlowNetwork network = createFlowNetwork(curTeam);
					if (network != null) {
						FordFulkerson fd = new FordFulkerson(network, 0, network.V()-1);
						if (fd.value() == tmpCurTeamMaxPossibleWin) {
							//do nothing
						}else {
							curTeam.setElimated(true);
							int firstTeamNode = network.V() - tmpAgainstTeamIndexes.length - 1;
							for (int i = 0; i < tmpAgainstTeamIndexes.length; i++) {
								int teamNodeToCheck = firstTeamNode + i;
								if (fd.inCut(teamNodeToCheck)) {
									curTeam.addElimationTeam(teamNames.get(tmpAgainstTeamIndexes[i]));
								}
							}
						}					
					}
				}
			}			
		}
	}
	
	public static void main(String[] args) {
		String fileName = ClassLoader.getSystemClassLoader().getResource("baseball/teams12.txt").toString();
	    BaseballElimination division = new BaseballElimination(fileName);
	    
	    for (String team : division.teams()) {
	        if (division.isEliminated(team)) {
	            StdOut.print(team + " is eliminated by the subset R = { ");
	            for (String t : division.certificateOfElimination(team)) {
	                StdOut.print(t + " ");
	            }
	            StdOut.println("}");
	        }
	        else {
	            StdOut.println(team + " is not eliminated");
	        }
	    }
	}
}
