function [w] = memory(dat)
%------------------------------------------------------------------------
% function [] = memory(dat)
%
% see wips.m.
% trying to do the same thing but for the memory array
%------------------------------------------------------------------------
global memHeapNorm
global memNonHeapNorm

% smf: Had an "Error: Undefined function or variable memNorm"
memHeapNorm = 1;
memNonHeapNorm = 1;

% Find end of data.

for i=1:length(dat.memoryHeap)
  j = length(dat.memoryHeap)-i+1;
  if (dat.memoryHeap(j)>0) 
    break;
  end
end

lenHeap = j;

for i=1:length(dat.memoryNonHeap)
  j = length(dat.memoryNonHeap)-i+1;
  if (dat.memoryNonHeap(j)>0) 
    break;
  end
end

lenNonHeap = j;

clf;
hold on;
l = plot(dat.memoryHeap(1:lenHeap)/memHeapNorm, 'r.');
l = plot(dat.memoryNonHeap(1:lenNonHeap)/memNonHeapNorm, 'b.');

avgMemHeap(1) = 0;
avgMemNonHeap(1) = 0;
for i=1:30
  avgMemHeap(1) = avgMemHeap(1) + dat.memoryHeap(i);
  avgMemNonHeap(1) = avgMemNonHeap(1) + dat.memoryNonHeap(i);
end

for i=31:lenHeap
  avgMemHeap(i-29) = avgMemHeap(i-30) + dat.memoryHeap(i) - dat.memoryHeap(i-30);
end

for i=31:lenNonHeap
  avgMemNonHeap(i-29) = avgMemNonHeap(i-30) + dat.memoryNonHeap(i) - dat.memoryNonHeap(i-30);
end

avgMemHeap = avgMemHeap / 30;
avgMemNonHeap = avgMemNonHeap / 30;

l = plot((30:lenHeap)-15, avgMemHeap/memHeapNorm, 'r-');
l = plot((30:lenNonHeap)-15, avgMemNonHeap/memNonHeapNorm, 'b-');

ax = axis;
s = (dat.startMI-dat.startRU)/1000;
e = (dat.startRD-dat.startRU)/1000;
plot([s,s], ax(3:4), 'k--');
plot([e,e], ax(3:4), 'k--');

s=floor(s);
e=ceil(e);

avgMemHeap = sum(dat.memoryHeap(s:e))/(e-s+1)/memHeapNorm;
plot([s,e], [avgMemHeap, avgMemHeap], 'r--');

avgMemNonHeap = sum(dat.memoryNonHeap(s:e))/(e-s+1)/memNonHeapNorm;
plot([s,e], [avgMemNonHeap, avgMemNonHeap], 'b--');

v=axis;
ymax=v(4)+5;
xmax = v(2);
axis([0 xmax 0 ymax]);

title(sprintf('Memory usage Over Time (Avg Heap/Non-Heap) = %9.2f/%9.2f', avgMemHeap, avgMemNonHeap)s);
%title('Throughput Over Time');
ylabel('Usage (%)');
xlabel('Time (s)');

