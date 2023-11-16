package graph

import (
	"fmt"
	"math/rand"
)

type Node struct {
	Name  string
	Edges *[]Edge
	Goal  bool
}

type Edge struct {
	Weight int
	To     *Node
	From   *Node
}

func generateR(start *Node, depth int, currDepth int) {
	degreeLevel := rand.Intn(4) + 1
	edges := make([]Edge, degreeLevel)
	goal := false
	if currDepth == depth-1 {
		goal = true
	} else if currDepth == depth {
		start.Edges = &[]Edge{}
		return
	}
	for deg := 1; deg <= degreeLevel; deg++ {
		edges[deg-1] = Edge{
			Weight: rand.Intn(20) + 1,
			To:     &Node{Name: start.Name + "." + fmt.Sprint(deg), Goal: goal},
		}
		generateR(edges[deg-1].To, depth, currDepth+1)
	}
	start.Edges = &edges
}

func Generate(depth int) *Node {
	start := Node{Name: "node"}
	generateR(&start, depth, 0)
	return &start
}

func printGraphR(start *Node, gap string) {
	ovrGap := gap
	if len(ovrGap) > 2 {
		ovrGap = ovrGap[:len(ovrGap)-3] + "|--"
	}
	fmt.Printf("%s%s\n", ovrGap, start.Name)
	gap += "|  "
	for index, edge := range *start.Edges {
		if edge.To == nil {
			continue
		}
		if index == len(*start.Edges)-1 {
			gap = gap[:len(gap)-3] + "   "
		}
		printGraphR(edge.To, gap)
	}
}

func PrintGraph(start *Node) {
	printGraphR(start, "")
}
