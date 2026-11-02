local key = KEYS[1]

local now = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local limit = tonumber(ARGV[3])
local requestId = ARGV[4]

local cutoff = now - window

redis.call('ZREMRANGEBYSCORE', key, '-inf', cutoff)

local requestCount = redis.call('ZCARD', key)

if requestCount >= limit then
    return 0
end

redis.call('ZADD', key, now, requestId)

redis.call('EXPIRE', key, math.ceil(window / 1000) * 2)

return 1
