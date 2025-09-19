    package unoeste.termo6.simulador.gramatica;

    public class Lista {
        private No cabeca;

        public Lista() {
            this.cabeca = null;
        }

        public boolean isEmpty() {
            return cabeca == null;
        }

        public void insere(No no){
            no.setProx(cabeca);
            cabeca=no;
        }

        public No remove(){
            No temp = cabeca;
            cabeca = cabeca.getProx();
            return temp;
        }

        public No getCabeca() {
            return cabeca;
        }

        public void setCabeca(No cabeca) {
            this.cabeca = cabeca;
        }

        public void excluir (No info){
            if (cabeca == info){
                cabeca = cabeca.getProx();
            }else{
                No aux = cabeca;
                while (aux.getProx()!=null && aux.getProx()!=info){
                    aux = aux.getProx();
                }
                if (aux.getProx()!=null) {
                    aux.setProx(aux.getProx().getProx());
                }
            }
        }
        public No pesquisa (String leitura){
            No aux = cabeca;
            while (aux!=null){
                if (aux.getLeitura().equals(leitura)){
                    return aux;
                }
                aux = aux.getProx();
            }
            return null;
        }
    }
