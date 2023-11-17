package main

import (
	"fmt"
	"path-search/graph"
)

func main() {
	root := graph.Generate(4)
	keys := dfs(root)
	fmt.Println(keys)
}

func dfs(start *graph.Node) []string {
	result := make([]string, 0)
	visited := make(map[string]bool)
	dfsr(start, visited, &result)
	keys := make([]string, 0)
	for key := range visited {
		keys = append(keys, key)
	}
	return keys
}

func dfsr(start *graph.Node, visited map[string]bool, result *[]string) {
	visited[start.Name] = true
	for _, edge := range *start.Edges {
		if !visited[edge.To.Name] {
			dfsr(edge.To, visited, result)
		}
	}
}
