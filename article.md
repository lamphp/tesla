使用netty构建API网关实践之路

######API网关的使用场景
  在内部使用微服务架构的前提下，客户端调用后端服务时，将面临登录、授权、流量、负载均衡、健康检查等操作，而且后端服务估计在技术选型上不单存在http的服务，RPC相关的内部服务也大量存在，在这种场景下，将这些操作交给统一的一个中间层来进行处理，降低系统之间的耦合，以使得微服务更加的专注于业务逻辑的处理，降低系统的响应时间。
  