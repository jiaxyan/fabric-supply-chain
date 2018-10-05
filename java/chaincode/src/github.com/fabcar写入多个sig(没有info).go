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
	"fmt"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	sc "github.com/hyperledger/fabric/protos/peer"
)

// Define the Smart Contract structure
type SmartContract struct {
}

// Define the car structure, with 4 properties.  Structure tags are used by encoding/json library
type Sig struct {
	signature []byte `json:"signature"`
}

/*
 * The Init method is called when the Smart Contract "fabcar" is instantiated by the blockchain network
 * Best practice is to have any Ledger initialization in separate function -- see initLedger()
 */
func (s *SmartContract) Init(APIstub shim.ChaincodeStubInterface) sc.Response {
	return shim.Success(nil)
}

/*
 * The Invoke method is called as a result of an application request to run the Smart Contract "fabcar"
 * The calling application program has also specified the particular smart contract function to be called, with arguments
 */
func (s *SmartContract) Invoke(APIstub shim.ChaincodeStubInterface) sc.Response {

	// Retrieve the requested Smart Contract function and arguments
	args := APIstub.GetArgs()
	function := string(args[0])
	//function, _ := APIstub.GetFunctionAndParameters()

	// Route to the appropriate handler function to interact with the ledger appropriately
	if function == "querySig" {
		return s.querySig(APIstub, args)
	} else if function == "createSig" {
		return s.createSig(APIstub, args)
	} else if function == "queryAllSigs" {
		return s.queryAllSigs(APIstub)
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
	return shim.Success(sigAsBytes)
}

func (s *SmartContract) createBatchSigs(APIstub shim.ChaincodeStubInterface, args [][]byte) sc.Response {
	argslen := len(args)
	if argslen%2 != 1 {
		return shim.Error("Expect The args Num in createBatchSigs would be odd number")
	}

	i := 1
	for i < (argslen - 1) {
		APIstub.PutState(string(args[i]), args[i+1])
		i = i + 2
	}
	return shim.Success(nil)
}

func (s *SmartContract) createSig(APIstub shim.ChaincodeStubInterface, args [][]byte) sc.Response { //args []string

	if len(args) != 3 { //这里第0个其实是function name 第1个是key  第2是value([]byte型)
		return shim.Error("Incorrect number of arguments. Expecting 3( [0]-funcName [1]-key [2]-value )")
	}

	//var sig = Sig{signature: args[1]}
	fmt.Println("=====increateSig====")
	//carAsBytes, _ := json.Marshal(car)
	//APIstub.PutState(args[0], carAsBytes)
	APIstub.PutState(string(args[1]), args[2])

	return shim.Success(nil)
}

func (s *SmartContract) queryAllSigs(APIstub shim.ChaincodeStubInterface) sc.Response {

	startKey := "SIG0"
	endKey := "SIG999"

	resultsIterator, err := APIstub.GetStateByRange(startKey, endKey)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	// buffer is a JSON array containing QueryResults
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"Key\":")
		buffer.WriteString("\"")
		buffer.WriteString(queryResponse.Key)
		buffer.WriteString("\"")

		buffer.WriteString(", \"Record\":")
		// Record is a JSON object, so we write as-is
		buffer.WriteString(string(queryResponse.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	fmt.Printf("================= queryAllSigs:\n%s\n", buffer.String())

	return shim.Success(buffer.Bytes())
}

// The main function is only relevant in unit test mode. Only included here for completeness.
func main() {

	// Create a new Smart Contract
	err := shim.Start(new(SmartContract))
	if err != nil {
		fmt.Printf("Error creating new Smart Contract: %s", err)
	}
}
