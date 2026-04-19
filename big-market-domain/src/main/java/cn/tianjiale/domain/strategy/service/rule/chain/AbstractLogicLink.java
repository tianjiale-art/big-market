package cn.tianjiale.domain.strategy.service.rule.chain;

public abstract class AbstractLogicLink<T,D,R> implements ILogicLink<T,D,R>{
        private ILogicLink<T,D,R> next;

        /**
         * 告诉我你的下一个节点是谁
         * @return
         */
        public ILogicLink<T,D,R> next(){
                return next;
        }

        /**
         * 串联下一个节点
         * @param next
         * @return
         */
        public ILogicLink<T,D,R> appendNext(ILogicLink<T,D,R> next){
                this.next = next;
                return next;
        }

        /**
         * 执行下一个节点的具体逻辑
         * @param requestParameter
         * @param dynamicContext
         * @return
         * @throws Exception
         */
        protected R next(T requestParameter,D dynamicContext)throws Exception{
              return   next.apply(requestParameter,dynamicContext);

        }
}
