/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * The sample smart contract for documentation topic:
 * Writing Your First Blockchain Application
 */

package main

/* Imports
 * 4 utility libraries for formatting, handling bytes, reading and writing JSON, and string manipulation
 * 2 specific Hyperledger Fabric specific libraries for Smart Contracts
 */
import (
	"bytes"
	"encoding/json"
	"fmt"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	sc "github.com/hyperledger/fabric/protos/peer"
)

// Define the Smart Contract structure
type SmartContract struct {
}

// Define the car structure, with 4 properties.  Structure tags are used by encoding/json library
type Sig struct {
	Info      string `json:"info"`
	Signature []byte `json:"signature"`
}

type MutiSig struct {
	Info      string `json:"info"`
	Signature []byte `json:"signature"`
}

func (s *SmartContract) Init(APIstub shim.ChaincodeStubInterface) sc.Response {
	return shim.Success(nil)
}

func (s *SmartContract) Invoke(APIstub shim.ChaincodeStubInterface) sc.Response {
	args := APIstub.GetArgs()
	function := string(args[0])
	if function == "querySig" {
		return s.querySig(APIstub, args)
	} else if function == "createSig" {
		return s.createSig(APIstub, args)
	} else if function == "createBatchSigs" {
		return s.createBatchSigs(APIstub, args)
	}
	return shim.Error("Invalid Smart Contract function name.(In Invoke function)")
}

func (s *SmartContract) querySig(APIstub shim.ChaincodeStubInterface, args [][]byte) sc.Response {

	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2 ([0] is func Name, [1] is key)")
	}
	sigAsBytes, _ := APIstub.GetState(string(args[1]))
	sig := &Sig{}
	json.Unmarshal(sigAsBytes, sig)
	var buffer bytes.Buffer
	res1 := []byte(sig.Info)
	res2 := sig.Signature
	buffer.Write(res2)
	buffer.Write(res1)
	resbytes := buffer.Bytes()
	return shim.Success(resbytes)
}

func (s *SmartContract) createSig(APIstub shim.ChaincodeStubInterface, args [][]byte) sc.Response { //args []string

	if len(args) != 3 { //这里第0个其实是function name 第1个是key  第2是value([]byte型)
		return shim.Error("Incorrect number of arguments. Expecting 3( [0]-funcName [1]- [2]-value )")
	}
	var sig = Sig{Info: string(args[1]), Signature: args[2]}

	sigAsBytes, _ := json.Marshal(sig)
	if len(sigAsBytes) <= 10 {
		return shim.Error("====The Generated sigAsBytes has only less than 10====")
	}
	APIstub.PutState(string(args[1]), sigAsBytes)

	return shim.Success(nil)
}

func (s *SmartContract) createBatchSigs(APIstub shim.ChaincodeStubInterface, args [][]byte) sc.Response {
	argslen := len(args)
	if argslen%2 != 1 {
		return shim.Error("Expect The args Num in createBatchSigs would be odd number")
	}

	i := 1
	for i < (argslen - 1) {
		var sig = Sig{Info: string(args[i]), Signature: args[i+1]}
		sigAsBytes, _ := json.Marshal(sig)
		APIstub.PutState(string(args[i]), sigAsBytes)
		i = i + 2
	}
	return shim.Success(nil)
}

// The main function is only relevant in unit test mode. Only included here for completeness.
func main() {

	// Create a new Smart Contract
	err := shim.Start(new(SmartContract))
	if err != nil {
		fmt.Printf("Error creating new Smart Contract: %s", err)
	}
}
