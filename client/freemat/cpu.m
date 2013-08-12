function [w] = cpu(dat)
%------------------------------------------------------------------------
% function [] = cpu(dat)
%
% see wips.m
% trying to do the same thing but for the memory array
%------------------------------------------------------------------------
global cpuNorm

% smf: Had an "Error: Undefined function or variable cpuNorm"
cpuNorm = 1;

% Find end of data.

for i=1:length(dat.cpu)
  j = length(dat.cpu)-i+1;
  if (dat.cpu(j)>0) 
    break;
  end
end

len = j;

clf;
hold on;
l = plot(dat.cpu(1:len)/cpuNorm, 'r.');

avg(1) = 0;
for i=1:30
  avg(1) = avg(1)  +dat.cpu(i);
end

for i=31:len
  avg(i-29) = avg(i-30) + dat.cpu(i) - dat.cpu(i-30);
end

avg = avg / 30;

l = plot((30:len)-15, avg/cpuNorm, 'k-');

ax = axis;
s = (dat.startMI-dat.startRU)/1000;
e = (dat.startRD-dat.startRU)/1000;
plot([s,s], ax(3:4), 'b--');
plot([e,e], ax(3:4), 'b--');

s=floor(s);
e=ceil(e);

avg = sum(dat.cpu(s:e))/(e-s+1)/cpuNorm;
plot([s,e], [avg, avg], 'g--');

title(sprintf('CPU usage Over Time (Avg = %9.2f)', avg));
%title('Throughput Over Time');
ylabel('Usage (%)');
xlabel('Time (s)');
