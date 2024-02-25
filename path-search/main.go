package main

import (
	"fmt"
	"path-search/graph"
	"path-search/non-heuristic"
)

func main() {
	root := graph.TestGraph()
	graph.PrintGraph(root)
  dfsKeys := non.DFSfirst(root)
  fmt.Println(dfsKeys)
  bfsKeys := non.BFS(root)
  fmt.Println(bfsKeys)
}
