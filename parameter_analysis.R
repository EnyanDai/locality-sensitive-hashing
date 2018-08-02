data<-read.table(file = file.choose(),header = TRUE,sep=" ")

data1<-subset(data,nShingles == 1000)
data2<-subset(data,nShingles == 10000)
data3<-subset(data,nShingles == 100000)

t.test(data1$precision,data2$precision,paired = TRUE,alternative = "less")
t.test(data2$precision,data3$precision,paired = TRUE,alternative = "less")
t.test(data1$recall,data2$recall,paired = TRUE)
t.test(data2$recall,data3$recall,paired = TRUE)
t.test(data1$time,data2$time,paired = TRUE)
t.test(data2$time,data3$time,paired = TRUE)
plm<-lm(precision~row+band+I(row*band),data = data2)
summary(plm)
plm<-lm(precision~row+I(row*band),data = data2)
summary(plm)

rlm<-lm(recall~row+band+I(row*band),data = data2)
summary(rlm)
rlm<-lm(recall~row+I(row*band),data = data2)
summary(rlm)

tlm<-lm(time~row+band+I(row*band),data = data2)
summary(tlm)
tlm<-lm(time~row+I(row*band),data = data2)
summary(tlm)
tlm<-lm(time~I(row*band),data = data2)
summary(tlm)


band2=subset(data2,band==2)
band3=subset(data2,band==3)
opar<-par(no.readonly = TRUE)
par(lwd=2,cex=1.5,font.lab=2)
plot(band2$row,band2$precision,pch=15,lty=1,type="b",col="red",main="band=2 vs band=3",xlab = "rows",ylab="precision")
lines(band3$row,band3$precision,type="b",pch=17,lty=2,col="blue")
legend("bottomright",inset=0.05,title="band",c("2","3"),lty=c(1,2),pch=c(15,17),col=c("red","blue"))

plot(band2$row,band2$recall,pch=15,lty=1,type="b",col="red",main="band=2 vs band=3",xlab = "rows",ylab="recall")
lines(band3$row,band3$recall,type="b",pch=17,lty=2,col="blue")
legend("topright",inset=0.05,title="band",c("2","3"),lty=c(1,2),pch=c(15,17),col=c("red","blue"))

plot(band2$row,band2$time,pch=15,lty=1,type="b",col="red",main="band=2 vs band=3",xlab = "rows",ylab="time")
lines(band3$row,band3$time,type="b",pch=17,lty=2,col="blue")
legend("topleft",inset=0.05,title="band",c("2","3"),lty=c(1,2),pch=c(15,17),col=c("red","blue"))

