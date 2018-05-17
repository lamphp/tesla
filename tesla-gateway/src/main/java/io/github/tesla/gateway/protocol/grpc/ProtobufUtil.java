/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tesla.gateway.protocol.grpc;

import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.MethodDescriptor;

import io.github.tesla.common.domain.ApiRpcDO;

/**
 * @author liushiming
 * @version GrpcRouteService.java, v 0.0.1 2018年1月7日 下午12:59:14 liushiming
 */

public class ProtobufUtil {


  public static Pair<Descriptor, Descriptor> resolveServiceInputOutputType(final ApiRpcDO rpcDo) {
    return findDirectyprotobuf(rpcDo);
  }

  private static Pair<Descriptor, Descriptor> findDirectyprotobuf(final ApiRpcDO rpcDo) {
    byte[] protoContent = rpcDo.getProtoContext();
    if (protoContent != null && protoContent.length > 0) {
      List<FileDescriptor> fileDescs =
          JSON.parseObject(new String(protoContent), new TypeReference<List<FileDescriptor>>() {});
      ServiceResolver serviceResolver = new ServiceResolver(fileDescs);
      ProtoMethodName protoMethodName = ProtoMethodName
          .parseFullGrpcMethodName(rpcDo.getServiceName() + "/" + rpcDo.getMethodName());
      MethodDescriptor protoMethodDesc = serviceResolver.resolveServiceMethod(protoMethodName);
      return new ImmutablePair<Descriptor, Descriptor>(protoMethodDesc.getInputType(),
          protoMethodDesc.getOutputType());
    }
    return null;
  }



}
