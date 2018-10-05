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
	"strconv"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	sc "github.com/hyperledger/fabric/protos/peer"
)

// Define the Smart Contract structure
type SmartContract struct {
}

// Define the car structure, with 4 properties.  Structure tags are used by encoding/json library
type Info struct {
	ProductName   string `json:"productName"`
	Supplier  string `json:"supplier"`
	ArrivedTime string `json:"arrivedTime"`
	Signature  string `json:"signature"`
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
	function, args := APIstub.GetFunctionAndParameters()
	// Route to the appropriate handler function to interact with the ledger appropriately
	if function == "queryInfo" {
		return s.queryInfo(APIstub, args)
	} else if function == "initLedger" {
		return s.initLedger(APIstub)
	} else if function == "createInfo" {
		return s.createInfo(APIstub, args)
	} else if function == "queryAllInfo" {
		return s.queryAllInfo(APIstub)
	}

	return shim.Error("[ChainCode ERROR] Invalid Smart Contract function name.")
}

func (s *SmartContract) queryInfo(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	if len(args) != 1 {
		return shim.Error("[ChainCode ERROR] Incorrect number of arguments(in queryInfo func). Expecting 1")
	}

	infoAsBytes, _ := APIstub.GetState(args[0])
	return shim.Success(infoAsBytes)
}

func (s *SmartContract) initLedger(APIstub shim.ChaincodeStubInterface) sc.Response {
	infos := []Info{
		Info{ProductName: "productA", Supplier: "supplierA", ArrivedTime: "1st", Signature: "asdcacvqwwvqwcadwdc"},
		Info{ProductName: "productA", Supplier: "supplierB", ArrivedTime: "2ed", Signature: "awcwdcqwvcqwwcra"},
		Info{ProductName: "productA", Supplier: "supplierC", ArrivedTime: "3th", Signature: "acwecwvawvcad"},
		Info{ProductName: "productA", Supplier: "supplierD", ArrivedTime: "4th", Signature: "acwecfaewverwvvvqwvqwdqfr"},
	}

	i := 0
	for i < len(infos) {
		fmt.Println("i is ", i)
		infoAsBytes, _ := json.Marshal(infos[i])
		APIstub.PutState("INFO"+strconv.Itoa(i), infoAsBytes)
		fmt.Println("Added", infos[i])
		i = i + 1
	}

	return shim.Success(nil)
}

func (s *SmartContract) createInfo(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	if len(args) != 5 {
		return shim.Error("[ChainCode ERROR] Incorrect number of arguments(in createInfo func). Expecting 5")
	}

	var info = Info{ProductName: args[1], Supplier: args[2], ArrivedTime: args[3], Signature: args[4]}

	infoAsBytes, _ := json.Marshal(info)
	APIstub.PutState(args[0], infoAsBytes)

	return shim.Success(nil)
}

func (s *SmartContract) queryAllInfo(APIstub shim.ChaincodeStubInterface) sc.Response {

	startKey := "INFO0"
	endKey := "INFO999"

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

	fmt.Printf("- queryAllInfos:\n%s\n", buffer.String())

	return shim.Success(buffer.Bytes())
}



// The main function is only relevant in unit test mode. Only included here for completeness.
func main() {

	// Create a new Smart Contract
	err := shim.Start(new(SmartContract))
	if err != nil {
		fmt.Printf("[ChainCode ERROR] Error creating new Smart Contract: %s", err)
	}
}
