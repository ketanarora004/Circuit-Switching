/**         PROJECT DETAILS
 * Author   :   Soham Kapur
 * Year     :   2023
 * Title    :   Circuit Switching using Dynamic Programming
 * Purpose  :   Undergraduate Graded Project
 * Course   :   Computer Networks
 * Desc     :   Simulation of Circuit Switching in Java to find optimal set of paths for multiple data transfers using Dynamic Programming.
 */

import java.util.*;
public class CircuitSwitchingDP
{
    ArrayList<Integer>[] adjList;
    static int n=0, k=0, s[], d[], min=Integer.MAX_VALUE;
    /* VARIABLES:
     * n: Number of data transfers
     * k: Counter for current Source and Destination pair being checked
     * s: Array to store all Souce nodes.
     * d: Array to store all Destination nodes.
     * min: Stores the minimum Longest path distance of the checked combinations ofpaths.
     */

    
    /*
     * This is the Graph (Network) consisting of nodes and edges expressed as an Adjacency Matrix, using a DDA.
     * The index value represents the nodes and the array elements at the coordinates represent the Edge weight between the 2 nodes.
     * The edge with direction from node u to node v will be at Graph[u][v].
     * The sample graph used here is an Undirected Graph, but a Directed Graph can also be used without any other change in the code.
     */
    int Graph[][] = { { 0, 4, 0, 0, 0, 0, 0, 8, 0 },
            { 4, 0, 8, 0, 0, 0, 0, 11, 0 },
            { 0, 8, 0, 7, 0, 4, 0, 0, 2 },
            { 0, 0, 7, 0, 9, 14, 0, 0, 0 },
            { 0, 0, 0, 9, 0, 10, 0, 0, 0 },
            { 0, 0, 4, 14, 10, 0, 2, 0, 0 },
            { 0, 0, 0, 0, 0, 2, 0, 1, 6 },
            { 8, 11, 0, 0, 0, 0, 1, 0, 7 },
            { 0, 0, 2, 0, 0, 0, 6, 7, 0 } };

    //This copy of the graph is created so that the user can use this code in a loop (not present in this code) without changing the original Graph.
    int altGraph[][] = Graph;
    //The mpdifications to the original Graph present in the Alternate Graph (altGraph) will be explained later in the code.

    int v = Graph[0].length;
    boolean visited[] = new boolean[v];
    static ArrayList<ArrayList<Integer>>[] allPaths; //Data structure to store all possible paths between 2 nodes
    static ArrayList<Integer>[] distances; //Data structure to store total distance or weight of each path between 2 given nodes
    static ArrayList<Integer>[] FinalPaths; //Data Structure to store the final set of paths that should be chosen

    // Constructor to initialise 'v', the number of nodes
    public CircuitSwitchingDP()
    {
        this.v = v;
        initAdjList();
    }

    // utility method to initialise adjacency lists
    void initAdjList()
    {
        int i;
        adjList = new ArrayList[v];
        for (i = 0; i < v; i++)
            adjList[i] = new ArrayList<>();

        allPaths = new ArrayList[n];
        for(i=0; i<n; i++)
            allPaths[i] = new ArrayList<>();

        distances = new ArrayList[n];

        FinalPaths = new ArrayList[n];
        for(i=0; i<n; i++)
            FinalPaths[i] = new ArrayList<>();
    }

    /* The graph is transferred from a DDA to an Array of ArrayLists.
     * This prevents checking of pairs of nodes that do not have any edge, thus improving speed in the following operations.
     */
    public void addEdges()
    {
        int n=v;
        for(int i=0; i<n; i++)
            for(int j=0; j<n; j++)
                if(Graph[i][j]>0)
                    adjList[i].add(j);
    }

    /* In the alternate graph, all incoming edges of every Source node and all outgoing edges of every Destination node are eliminated.
     * This reduces the number of edges that the program has to check, thus improving speed and efficiency.
     */
    public void AltGraph()
    {
        for(int i=0; i<n; i++)
            for(int j=0; j<9; j++)
            {
                altGraph[j][s[i]] = 0;
                altGraph[d[i]][j] = 0;
            }
    }

    // Parts of this function have been taken from the code for finding All Paths in a Graph, available on "Geeks For Geeks" 
    public void getAllPaths(int s, int d)
    {
        boolean[] isVisited = new boolean[v];
        ArrayList<Integer> pathList = new ArrayList<>();
        // add source to path[]
        pathList.add(s);
        // Call utility function
        getAllPathsUtil(s, d, isVisited, pathList, 0);
    }

    // Parts of this function have been taken from the code for finding All Paths in a Graph, available on "Geeks For Geeks" 
    private void getAllPathsUtil(Integer u, Integer d, boolean[] isVisited, ArrayList<Integer> localPathList, int distance)
    {
        /* An if condition that prevents the checking of some extra paths by terminating the recursion if the destination node is adjacent to the current node.
         * This prevents the checking of some useless paths and saves time.
         * It assumes that the direct edge between any two nodes is the shortest possible path between them.
         * This condition is only useful when all edges have positive weights.
         */
        if(altGraph[u][d]>0)
        {
            localPathList.add(d);
            allPaths[k].add(new ArrayList<>(localPathList));
            distances[k].add(distance + altGraph[u][d]);
            localPathList.remove(d);
            return;
        }
        isVisited[u] = true;
        // This loop having recursion checks the possibility of creating a path between the Source and Destination using every node.
        for (Integer i : adjList[u])
        {
            if (!isVisited[i])
            {
                // store current node in path
                localPathList.add(i);
                distance += altGraph[u][i];
                getAllPathsUtil(i, d, isVisited, localPathList, distance);
                distance -= altGraph[u][i];
                // remove current node in path
                localPathList.remove(i);
            }
        }
        isVisited[u] = false;
    }

    //This function uses Bubble Sort to sort the paths between every pair of Source and Destination nodes with respect to total distance/weight.
    void sortPaths()
    {
        for(int h=0; h<n; h++)
        {    
            int l = allPaths[h].size();
            for (int i = 0; i < l-1; i++)
                for (int j = 0; j < l-i-1; j++)
                    if (distances[h].get(j) > distances[h].get(j+1))
                    {
                        Collections.swap(allPaths[h], j, j+1);
                        Collections.swap(distances[h], j, j+1);
                    }
        }
    }

    //This function prints all the paths between every pair of Source and Destination nodes and their respective total distances. 
    public void printPaths()
    {
        for(int i=0; i<n; i++)
        {
            System.out.println("\nAll paths from " + s[i] + " to " + d[i] + " are:");
            System.out.println("\tDistance\tPaths");
            for(int j=0; j<allPaths[i].size(); j++)
                System.out.println("\t   " + distances[i].get(j) + "\t\t" + allPaths[i].get(j));
        }
    }

    /* This is the function where the Dynamic Programming takes place.
     * This function iterates through all possible combinations of paths.
     * It returns the combination in which the Longest path is the smallest as compared to the Longest Paths in other combinations.
     * Other conditions, such as Smallest Mean, Smallest Median or other statistical values using all paths in a combination, can also be used as required.
     * The program will have to be modified to accomodate other or more conditions.
     * The purpose of this function is to find the most optimal combination of paths as per the set condition for optimality.
     */
    void AltPaths(int p, int max, ArrayList<Integer>[] finalPath)
    {
        if(p==n)
        {
            // This is the if statement that finalises the combination based on the set condition.
            if(min>max)
            {
                System.out.println();
                min = max;
                finalise(finalPath);
                System.out.println(finalPath);
            }
            return;
        }
        int i, j, l = allPaths[p].size();
        //This loop recursively creates all combinations of paths to ultimately check the optimality condition.
        for(i=0; i<l; i++)
        {
            if(checkIntersect(allPaths[p].get(i)))
                continue;
            visit(allPaths[p].get(i), p);
            max = Math.max(max, distances[p].get(i));
            finalPath[p] = new ArrayList<>();
            finalPath[p] = allPaths[p].get(i);
            AltPaths(p+1, max, finalPath);
            unvisit(finalPath[p], p);
            finalPath[p].clear();
        }
    }
    
    //This function checks if any node in the selected path is already present in a previously selected path.
    boolean checkIntersect(ArrayList<Integer> path)
    {
        for(int i=1; i<path.size()-1; i++)
            if(visited[path.get(i)])
                return true;
        return false;
    }

    // This functions visits all nodes in a path so that they can be checked for intersection of another path.
    void visit(ArrayList<Integer> path, int p)
    {
        for(int i=1; i<path.size()-1; i++)
            visited[path.get(i)] = true;
    }

    // This function 'unvisits' all nodes in a path once that paths is deleted from the current combination.
    void unvisit(ArrayList<Integer> path, int p)
    {
        for(int i=1; i<path.size()-1; i++)
            visited[path.get(i)] = false;
    }

    // This function adds the selected combination to a global variable to be accessed later on.
    void finalise(ArrayList<Integer>[] finalPath)
    {
        for(int i=0; i<n; i++)
            FinalPaths[i] = new ArrayList<>(finalPath[i]);
    }

    // This funciton prints the finalised combination, and if there is no data then prints the appropriate message.
    void printFinal()
    {
        System.out.println("Max distance: " + min + "\nFinal list of paths is: ");
        for(int i=0; i<n; i++)
        {
            if(FinalPaths[i].size()==0)
            {
                System.out.println("The given data transfers cannot occur simultaneously.");
                break;
            }
            else
                System.out.println(FinalPaths[i]);
        }
    }

    // Driver code
    public static void main()
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter no. of paths to travel:");
        n = sc.nextInt();
        s = new int[n];
        d = new int[n];
        ArrayList <Integer>[] finalPath = new ArrayList[n];
        int i, j;
        for(i=0; i<n; i++)
            finalPath[i] = new ArrayList<>();

        CircuitSwitchingDP g = new CircuitSwitchingDP();
        for(i=0; i<n;i++)
        {
            System.out.println("Enter Source " + (i+1) + ":");
            s[i] = sc.nextInt();
            System.out.println("Enter Destination " + (i+1) + ":");
            d[i] = sc.nextInt();
        }
        g.AltGraph();
        g.addEdges();
        for(i=0; i<n; i++)
        {
            k=i;
            distances[i] = new ArrayList<>();
            g.getAllPaths(s[i], d[i]);
        }
        System.out.println("\n\nSorted Paths: ");
        g.sortPaths();
        g.printPaths();
        System.out.println("\n");
        g.AltPaths(0, 0, finalPath);
        g.printFinal();
    }
}