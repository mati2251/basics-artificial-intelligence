package main

import (
	"path-search/graph"
)

func main() {
	root := graph.Generate(5)
	keys := dfsFirst(root)
	graph.PrintGraphMark(root, &keys)
}

func dfsFirst(start *graph.Node) []string {
	result := make([]string, 0)
	visited := make(map[string]bool)
	goal := false
	dfsrFirst(start, visited, &result, &goal)
	return result
}

func dfsrFirst(start *graph.Node, visited map[string]bool, result *[]string, goal *bool) {
	visited[start.Name] = true
	*result = append(*result, start.Name)
	for _, edge := range *start.Edges {
		if !visited[edge.To.Name] && !*goal {
			dfsrFirst(edge.To, visited, result, goal)
		}
	}
	if start.Goal || *goal {
		*goal = true
		return
	}
	*result = (*result)[:len(*result)-1]
}
